package com.renjia.rpc.apply;

import com.renjia.rpc.apply.handle.RequestHandler;
import com.renjia.rpc.core.HttpClient;
import com.renjia.rpc.protocol.RpcProtocol;
import org.springframework.context.ApplicationListener;
import org.springframework.web.client.RestClientException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class RequestHttpClient extends HttpClient implements ApplicationListener<RequestHandler.ChaiNode> {

    private RequestHandler.ChaiNode requestChai;

    @Override
    protected RpcProtocol doRequest(RpcProtocol rpcProtocol) {

        RpcProtocol.Body build = RpcProtocol.Body.builder().build();
        rpcProtocol.setBody(build);
        try {
            CompletableFuture<RpcProtocol> complet = new CompletableFuture<>();
            requestChai.handle(rpcProtocol, complet);
            rpcProtocol = complet.get();
        } catch (RestClientException | InterruptedException | ExecutionException e) {
            rpcProtocol.getBody().setThrowable(e);
        }

        return rpcProtocol;
    }

    @Override
    public void onApplicationEvent(RequestHandler.ChaiNode chaiNode) {
        this.requestChai = chaiNode;
    }
}
