package ai.openfabric.api.service;

import ai.openfabric.api.config.DockerConfig;
import ai.openfabric.api.model.Worker;
import ai.openfabric.api.repository.WorkerRepository;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ListContainersCmd;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ContainerNetwork;
import com.github.dockerjava.api.model.ContainerNetworkSettings;
import com.github.dockerjava.api.model.Statistics;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
public class WorkerService {

    @Autowired
    private WorkerRepository workerRepository;

    DockerClient dockerClient = DockerConfig.configure();

    public List<Worker> fetchWorkers(){
        ListContainersCmd listContainersCmd = dockerClient.listContainersCmd().withShowAll(true);
        List<Worker> workers = new ArrayList<>();
        for (Container container : listContainersCmd.exec()) {
            ContainerNetworkSettings networkSettings = container.getNetworkSettings();

            ContainerNetwork network = null;

            for (Map.Entry<String, ContainerNetwork> entry : networkSettings.getNetworks().entrySet()) {
                String key = entry.getKey();
                network = entry.getValue();
            }

            Worker worker = new Worker();

            worker.setId(container.getId());
            worker.setCommand(container.getCommand());
            worker.setImage(container.getImage());
            worker.setImageId(container.getImageId());
            worker.setState(container.getState());
            worker.setStatus(container.getStatus());
            worker.setLabels(container.getLabels().toString());
            worker.setName(Arrays.toString(container.getNames()));
            worker.setPorts(Arrays.toString(container.getPorts()));
            worker.setHostConfig(Objects.requireNonNull(container.getHostConfig()).toString());

            workers.add(worker);

        }
        return workers;
    }

    @PostConstruct
    public void updateDB(){
        for (Worker worker:
                fetchWorkers()) {
            Optional<Worker> optionalWorker = workerRepository.findById(worker.id);

            if (optionalWorker.isPresent()){
                continue;
            }

            workerRepository.save(worker);
        }
    }

    public ResponseEntity<String> getAllWorkers(int offset, int pageSize) {
        try {
            updateDB();

            Page<Worker> workerPage = workerRepository.findAll(PageRequest.of(offset, pageSize));

            Gson gson = new Gson();
            return new ResponseEntity<>(gson.toJson(workerPage.getContent()), HttpStatus.OK);
        } catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> startWorker(String containerId) {
        try {
            ListContainersCmd listContainersCmd = dockerClient.listContainersCmd().withShowAll(true);
            boolean ifExists = false;
            for (Container container : listContainersCmd.exec()) {
                if (Objects.equals(container.getId(), containerId)) {
                    ifExists = true;

                    dockerClient.startContainerCmd(containerId).exec();

                    break;
                }
            }

            if (!ifExists) {
                return new ResponseEntity<>("Worker could not be found!", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>("Worker started!", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> stopWorker(String containerId){
        try {
            ListContainersCmd listContainersCmd = dockerClient.listContainersCmd().withShowAll(true);
            boolean ifExists = false;
            for (Container container: listContainersCmd.exec()) {
                if (Objects.equals(container.getId(), containerId)){
                    ifExists = true;

                    dockerClient.stopContainerCmd(containerId).exec();

                    break;
                }
            }

            if (!ifExists){
                return new ResponseEntity<>("Worker could not be found!", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>("Worker stopped!", HttpStatus.OK);
        } catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> getWorkerInfo(String containerId){
        try {
            ListContainersCmd listContainersCmd = dockerClient.listContainersCmd().withShowAll(true);
            Container requestedContainer = null;
            boolean ifExists = false;
            for (Container container: listContainersCmd.exec()) {
                if (Objects.equals(container.getId(), containerId)){
                    ifExists = true;

                    requestedContainer = container;

                    break;
                }
            }

            if (!ifExists){
                return new ResponseEntity<>("Worker could not be found!", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(new Gson().toJson(requestedContainer), HttpStatus.OK);
        } catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> getWorkerStats(String containerId){
        try {
            ListContainersCmd listContainersCmd = dockerClient.listContainersCmd().withShowAll(true);
            Statistics stats = null;

            boolean ifExists = false;
            for (Container container: listContainersCmd.exec()) {
                if (Objects.equals(container.getId(), containerId)){
                    ifExists = true;

                    AsyncResultCallback<Statistics> callback = new AsyncResultCallback<Statistics>();
                    dockerClient.statsCmd(containerId).exec(callback);
                    try {
                        stats = (Statistics) callback.awaitResult();
                        callback.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                    }

                    break;
                }
            }
            if (!ifExists){
                return new ResponseEntity<>("Worker could not be found!", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(new Gson().toJson(stats), HttpStatus.OK);
        } catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
