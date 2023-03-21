package ai.openfabric.api.service;

import com.github.dockerjava.api.model.DockerObject;
import com.github.dockerjava.core.InvocationBuilder;

import java.io.IOException;

public class AsyncResultCallback<S extends DockerObject> extends InvocationBuilder.AsyncResultCallback {

    public AsyncResultCallback() {
        super();
    }

    @Override
    public void onNext(Object object) {
        super.onNext(object);
    }

    @Override
    public void close() throws IOException {
        super.close();
    }

    @Override
    public Object awaitResult() {
        return super.awaitResult();
    }
}
