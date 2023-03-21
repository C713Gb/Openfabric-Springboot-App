package ai.openfabric.api.controller;

import ai.openfabric.api.service.WorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${node.api.path}/worker")
public class WorkerController {

    @Autowired
    private WorkerService workerService;

    @PostMapping(path = "/hello")
    public @ResponseBody String hello(@RequestBody String name) {
        return "Hello!" + name;
    }

    @GetMapping(path = "/getAllWorkers/{offset}/{pageSize}")
    public @ResponseBody ResponseEntity<String> getAllWorkers(@PathVariable int offset, @PathVariable int pageSize){
        return workerService.getAllWorkers(offset, pageSize);
    }

    @PostMapping(path = "/startWorker")
    public @ResponseBody ResponseEntity<String> startWorker(@RequestBody String containerId) {
        return workerService.startWorker(containerId);
    }

    @PostMapping(path = "/stopWorker")
    public @ResponseBody ResponseEntity<String> stopWorker(@RequestBody String containerId) {
        return workerService.stopWorker(containerId);
    }

    @PostMapping(path = "/getWorkerInfo")
    public @ResponseBody ResponseEntity<String> getWorkerInfo(@RequestBody String containerId) {
        return workerService.getWorkerInfo(containerId);
    }

    @PostMapping(path = "/getWorkerStats")
    public @ResponseBody ResponseEntity<String> getWorkerStats(@RequestBody String containerId) {
        return workerService.getWorkerStats(containerId);
    }

}
