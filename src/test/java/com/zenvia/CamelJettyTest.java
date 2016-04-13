package com.zenvia;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jetty.JettyHttpComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

@RunWith(BlockJUnit4ClassRunner.class)
public class CamelJettyTest {

    @Test
    public void testCamelJetty() throws Exception {
        CamelContext context = new DefaultCamelContext();
        context.addRoutes(new RouteBuilder() {

            @Override
            public void configure() throws Exception {
                JettyHttpComponent jettyComponent = this.getContext().getComponent("jetty", JettyHttpComponent.class);
                jettyComponent.setMinThreads(1);
                jettyComponent.setMaxThreads(10);

                from("timer://foo?period=1000")
                        .setHeader(Exchange.HTTP_URI, constant("https://www.google.com.br/?gfe_rd=cr&ei=qq4OV6rLH-Sp8wezhqpI&gws_rd=ssl"))
                        .toF("jetty:http://local?httpClient.connectTimeout=%d&httpClient.timeout=%d&httpClient.maxRedirects=1",
                                30000,
                                30000)
                        .log("${headers.CamelHttpUri}")
                        .log("*** the body is = [${body}]")
                        .end();

            }
        });

        context.start();
        Thread.sleep(2000);
        context.stop();
    }
}
