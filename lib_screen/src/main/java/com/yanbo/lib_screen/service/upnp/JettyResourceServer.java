/*
 * Copyright (C) 2014 Kevin Shen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yanbo.lib_screen.service.upnp;


import com.yanbo.lib_screen.VConstants;
import com.yanbo.lib_screen.utils.LogUtils;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

import java.net.BindException;
import java.util.logging.Logger;

public class JettyResourceServer implements Runnable {
    final private static Logger log = Logger.getLogger(JettyResourceServer.class.getName());

    private Server mServer;

    public JettyResourceServer() {
    }

    private ServletContextHandler getServletContextHandler() {
        final ServletContextHandler servletContextHandler = new ServletContextHandler();
        servletContextHandler.setContextPath("/");
        servletContextHandler.setInitParameter("org.eclipse.jetty.servlet.Default.gzip", "false");

        servletContextHandler.addServlet(AudioResourceServlet.class, "/audio/*");
        servletContextHandler.addServlet(ImageResourceServlet.class, "/image/*");
        servletContextHandler.addServlet(VideoResourceServlet.class, "/video/*");
        return servletContextHandler;
    }

    synchronized public void startIfNotRunning() {
        if (mServer == null || (!mServer.isStarted() && !mServer.isStarting())) {
            log.info("Starting JettyResourceServer");
            try {
                int basePort = VConstants.JETTY_SERVER_PORT;
                for (int port = basePort; port < basePort + 10; port++) {
                    try {
                        mServer = new Server(port); // Has its own QueuedThreadPool
                        log.info("Starting JettyResourceServer "+port);
                        mServer.setGracefulShutdown(1000); // Let's wait a second for ongoing transfers to complete
                        mServer.setHandler(getServletContextHandler());
                        mServer.start();
                        VConstants.JETTY_SERVER_CURRENT_PORT = port;
                        log.info("JettyResourceServer port =" + port);
                        break;
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (!(e instanceof BindException))
                            if (e.getMessage() == null || !e.getMessage().contains("port")) {
                                throw e;
                            }
                    }
                }

            } catch (Exception ex) {
                log.severe("Couldn't start Jetty server: " + ex);
                throw new RuntimeException(ex);
            }
        }
    }

    synchronized public void stopIfRunning() {
        if (!mServer.isStopped() && !mServer.isStopping()) {
            log.info("Stopping JettyResourceServer");
            try {
                mServer.stop();
            } catch (Exception ex) {
                log.severe("Couldn't stop Jetty server: " + ex);
                throw new RuntimeException(ex);
            }
        }
    }

    public String getServerState() {
        if (mServer != null)
            return mServer.getState();
        return null;
    }

    @Override
    public void run() {
        startIfNotRunning();
    }

}
