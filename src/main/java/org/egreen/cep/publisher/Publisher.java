package org.egreen.cep.publisher;

import org.apache.axis2.transport.http.server.HttpFactory;
import org.wso2.carbon.databridge.agent.AgentHolder;
import org.wso2.carbon.databridge.agent.DataPublisher;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAgentConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAuthenticationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointException;
import org.wso2.carbon.databridge.commons.Event;
import org.wso2.carbon.databridge.commons.exception.TransportException;
import org.wso2.carbon.databridge.commons.utils.DataBridgeCommonsUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Created by dewmal on 9/26/15.
 */
public class Publisher {
    private static final String HTTPD_LOG_STREAM = "foracasting_stream";
    private static final String VERSION = "1.0.0";
    private static final String SAMPLE_LOG_PATH = "";
    private static String url = "52.16.226.123";
    private static String username = "admin";
    private static String password = "admin";
    private static int defaultThriftPort = 9175;
    private static int defaultBinaryPort = 7645;

    public static void main(String[] args) throws DataEndpointException, DataEndpointAuthenticationException, DataEndpointAgentConfigurationException, TransportException, DataEndpointConfigurationException {
        AgentHolder.setConfigPath(getDataAgentConfigPath());
        String host = getLocalAddress().getHostAddress();
        String type = getProperty("type", "Thrift");
        int receiverPort = defaultThriftPort;
        if (type.equals("Binary")) {
            receiverPort = defaultBinaryPort;
        }
        int securePort = receiverPort + 100;
        String url = getProperty("url", "tcp://" + host + ":" + receiverPort);
        String authURL = getProperty("authURL", "ssl://" + host + ":" + securePort);
        String username = getProperty("username", "admin");
        String password = getProperty("password", "admin");
        DataPublisher dataPublisher = new DataPublisher(type, url, authURL, username, password);
        String streamId = DataBridgeCommonsUtils.generateStreamId(HTTPD_LOG_STREAM, VERSION);
//        publishLogEvents(dataPublisher, streamId);
        dataPublisher.shutdown();
    }


    private static void publishLogEvents(DataPublisher dataPublisher, String streamId) throws
            FileNotFoundException {
        Scanner scanner = new Scanner(new FileInputStream(SAMPLE_LOG_PATH));
        int i = 1;
        while (scanner.hasNextLine()) {
            System.out.println("Publishlogevent:" + i);
            String aLog = scanner.nextLine();
            String[] aLogElements = aLog.split("\\s");
/*
aLogElements[0]->remoteIp
aLogElements[3]->request_date
aLogElements[6]->request
aLogElements[8]->httpcode
aLogElements[9]->length
*/
            Event event = new Event(streamId, System.currentTimeMillis(), new
                    Object[]{"external"}, null,
                    new Object[]{
                            aLogElements[0], aLogElements[3], aLogElements[6],
                            aLogElements[8], aLogElements[9]
                    });
            dataPublisher.publish(event);
            i++;
        }
        scanner.close();
    }


    private static HttpFactory getLocalAddress() {
        return null;
    }

    private static String getProperty(String type, String thrift) {
        return null;
    }

    private static String getDataAgentConfigPath() {
        return null;
    }
}
