import java.util.regex.*;


public class Request {

    public static String supportedHTTPMethods[] = {"GET", "HEAD"};
    public boolean isValid;
    public String httpMethodType;
    public String hostName;
    public String uriPath;

    public Request(String requestString) {

        //Parse httpMethod from input string
        for (String httpMethod : this.supportedHTTPMethods){
            if(requestString.contains(httpMethod)){
                this.httpMethodType = httpMethod;
                break;
            }
        }

        //Parse hostName information from input string
        String hostKey = "Host: ";
        if  (requestString.contains(hostKey)){
            int hostStartIndex = requestString.indexOf(hostKey);
            int hostEndIndex = requestString.indexOf('\r', hostStartIndex);
            this.hostName = requestString.substring(hostStartIndex, hostEndIndex);
        }

        //Parse uriPath for requested asset
        if(requestString.contains(this.httpMethodType) && requestString.contains(" HTTP")){
            this.uriPath = requestString.substring(requestString.indexOf("/"), requestString.indexOf(" HTTP"));
        }

        //Check if request format is valid
        this.isValid = (this.httpMethodType == null || this.hostName == null || this.uriPath == null) ? false : true;

    }


    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");

        result.append(this.getClass().getName() + " Object {" + NEW_LINE);
        result.append("Method: " + httpMethodType + NEW_LINE);
        result.append("Host: " + hostName + NEW_LINE);
        result.append("Path: " + uriPath + NEW_LINE );
        result.append(" isValid: " + isValid);

        return result.toString();
    }
}
