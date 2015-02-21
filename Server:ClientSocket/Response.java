import java.io.*;
import java.net.URISyntaxException;
import java.util.*;
import java.util.regex.*;

public class Response {

    Request request;
    int status;
    byte[] content;

    //Create hashmaps to house headers and redirects
    Map<String, String> headerMap = new HashMap<>();
    Map<String, String> redirectMap = new HashMap<>();

    //Create hashmap to house mime types
    static Map<String, String> mimes = new HashMap<>();{
        mimes.put(".html", "text/html");
        mimes.put(".txt", "text/plain");
        mimes.put(".pdf", "application/pdf");
        mimes.put(".png", "image/png");
        mimes.put(".jpeg", "image/jpeg");
    }

    //Create hashmap to house status codes
    static Map<Integer, String> codes = new HashMap<>();{
        codes.put(200, "OK");
        codes.put(301, "Moved Permanently");
        codes.put(403, "Forbidden");
        codes.put(404, "Not Found");
    }

    public Response(Request request) {
        this.request = request;
    }

    //Responsible for converting a response based on the status
    public byte[] convertResponse() throws FileNotFoundException{

        createResponse();

        headerMap.put("Content-Length:", Integer.toString(content.length));

        //Get status and assign appropriate code
        String scString = Response.codes.get(status);
        System.out.println("\nStatus: " +status+ "\n");

        //Creates the response string
        String response = "HTTP/1.1 " +status + " " + scString + "\r\n";
        Set<String> headerKeys = headerMap.keySet();

        for (String key : headerKeys) {
            response += key + " " + headerMap.get(key) + "\r\n";
        }

        response += "\r\n";

        if (request.httpMethodType.equals("HEAD")) {
            content = new byte[0];
        }

        byte[] responseBytes = new byte[response.length() + content.length];

        for (int i = 0; i < response.length(); ++i) {
            responseBytes[i] = (byte)response.charAt(i);
        }

        for (int i = response.length(); i < response.length() + this.content.length; ++i) {
            responseBytes[i] = this.content[i - response.length()];
        }

        return responseBytes;
    }

    //Creates a status
    private void createResponse() throws FileNotFoundException{

        //Checks if 404
        Boolean isValidMime = false;
        Iterator<String> keySetIterator = mimes.keySet().iterator();
        //Iterates through valid mime types
        while(keySetIterator.hasNext()){
            String key = keySetIterator.next();
            System.out.println("\nKey: " +key+ "\n");
            System.out.println(request.uriPath);

            //Set to true if a valid mime type is found
            if(request.uriPath == null || request.uriPath.endsWith(key)){
                isValidMime = true;
            }
            System.out.println("isVM: " +isValidMime);
        }
        //If a valid mime type is never found, return 404
        if(!isValidMime){
            System.out.println("isVM FALSE\n");
            returnStatus(404);
            return;
        }


        //Checks if 403
        if (!request.isValid) {
            returnStatus(403);
            return;
        }

        redirectRead();

        //Checks if 301
        if (redirectMap.containsKey(request.uriPath)) {
            returnStatus(301);
            content = new byte[0];
            headerMap.put("Location:", redirectMap.get(request.uriPath));
            return;
        }

        //If address is invalid, return 404
        File resource = null;
        try {
            resource = new File(ClassLoader.getSystemResource("www" + this.request.uriPath).toURI());

        } catch (URISyntaxException | NullPointerException e) {
            returnStatus(404);
            System.out.println("INVALID URL");
            return;
        }

        //Checks if 200
        if (resource.exists() && !resource.isDirectory()) {
            returnStatus(200);

            //Sets the file type and size to be returned
            setContentType(request.uriPath);
            FileInputStream inputStream;
            content = new byte[(int)resource.length()];

            int bytesRead, bufferIndex = 0;

            try {
                inputStream = new FileInputStream(resource);

                do {
                    bytesRead = inputStream.read(this.content, bufferIndex, (int)resource.length());
                    bytesRead += bufferIndex;
                } while (bytesRead > 0);

            } catch (FileNotFoundException e) {
                System.out.println("File not found: " + request.uriPath);
            } catch (Exception e) {
                System.out.println("An error occurred while loading: " + request.uriPath);
            }

        } else {
            returnStatus(404);
        }
    }

    //Reads in the redirect file
    private void redirectRead() throws FileNotFoundException{
        try {
            File redirectFile = new File(ClassLoader.getSystemResource("www/redirect.defs").toURI());
            Scanner redirectScanner = new Scanner(redirectFile);

            while (redirectScanner.hasNextLine()) {
                redirectScanner.findInLine("(.+)\\s(.+)");
                MatchResult match = redirectScanner.match();
                redirectMap.put(match.group(1), match.group(2));
                redirectScanner.nextLine();
            }

        } catch (URISyntaxException e) {
            System.out.println("Could not find redirect file.");
        }
    }

    //Set the content type of requested file
    private void setContentType(String path) {
        Scanner filetypeScanner = new Scanner(path);
        filetypeScanner.findInLine("(\\.[A-Za-z0-9]+)$");

        String extension = filetypeScanner.match().group(1);
        String mimeType = Response.mimes.get(extension);

        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }

        headerMap.put("Content-Type:", mimeType);
    }

    //Sets the status and content of a page
    private void returnStatus(int sc){
        if(sc == 200){
            status = 200;
        }

        if(sc == 301){
            status = 301;
        }

        if(sc == 404){
            status = 404;
            content = ("<html><head><title>Not Found</title></head>\n" +
                    "<body><h1>The file you requested was not found.</h1></body></html>\n").getBytes();
            headerMap.put("Content-Type:", "text/html");
        }

        if(sc == 403) {
            status = 403;
            content = ("<html><head><title>Not Recognized</title></head>\n" +
                    "<body><h1>The request was not recognized by the server.</h1></body></html>\n").getBytes();
            headerMap.put("Content-Type:", "text/html");
        }
    }
}