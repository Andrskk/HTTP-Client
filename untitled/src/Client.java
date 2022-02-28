import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Client {

    public static void getRequest(String uri, String header, boolean v) throws IOException
    {
        boolean startPrintingFlag = false;
        boolean oneLF = false;

        String[] splitedURI = uri.split("/get");
        String host = splitedURI[0];
        String url = "get" + splitedURI[1];

        Socket socket = new Socket(host, 80);

        InputStream response = socket.getInputStream();
        OutputStream request = socket.getOutputStream();

        byte[] data = ("GET /" + url + " HTTP/1.1\n" + "Host: " + host + "\n" + header + "\n" + "\n\n").getBytes();

        request.write(data);

        int c;

        if (v==false)
        {
            while ((c = response.read()) != -1)
            {
                if (oneLF==true && c==13)startPrintingFlag=true;
                else
                {
                    oneLF=false;
                }
                if (c==10)oneLF=true;

                if (startPrintingFlag==true) System.out.print((char) c);
            }
        }
        else
        {
            while ((c = response.read()) != -1)
            {
                System.out.print((char) c);
            }
        }
            socket.close();
    }

    public static void postRequest(String host, String header, String body) throws IOException
    {

        Socket socket = new Socket(host, 80);

        InputStream response = socket.getInputStream();
        OutputStream request = socket.getOutputStream();

        byte[] data = ("POST /post HTTP/1.1\n" + "Host: " + host + "\n"
                + header
                + "Content-length: " + body.length() + "\r\n"
                + "\r\n"
                + body + "\n\n").getBytes();

        request.write(data);

        int c;

        while ((c = response.read()) != -1)
        {
            System.out.print((char) c);
        }
    }

    public static void main(String[] args) throws IOException
    {
        String input="";
        String header="";
        String body="";
        boolean dFlag=false;
        boolean fFlag=false;
        boolean vFlag=false;

        Scanner scanner = new Scanner(System.in);
        input = scanner.nextLine();

        String[] splitedinput = input.split(" ");

        System.out.println();

        if (!splitedinput[0].equals("httpc"))
        {
            System.out.println("Unknown command: " + splitedinput[0]);
            System.exit(0);
        }
        if (splitedinput.length<3)
        {
            System.out.println("Error: No URL or Method");
            System.exit(0);
        }


        for (int i=0;i<splitedinput.length;i++)
        {
            if (splitedinput[i].equals("-d"))dFlag=true;
            if (splitedinput[i].equals("-f"))fFlag=true;
        }

        if (dFlag==true && fFlag==true)
        {
            System.out.println("Error: -d and -f cannot be usedd together");
            System.exit(0);
        }

        if (splitedinput[1].equals("help"))
        {

            if (splitedinput.length==2)
            {
                System.out.println("httpc is a curl-like application but supports HTTP protocol only.\n"
                        + "Usage:\n" + "\t httpc command [arguments]\n"
                        + "The commands are:\n" + "\t get executes a HTTP GET request and prints the response.\n"
                        + "\t post executes a HTTP POST request and prints the response.\n"
                        + "\t help prints this screen.\n" + "Use \"httpc help [command]\" for more information about a command.\n");
            }
            else
            {
                if (splitedinput[2].equals("get"))
                    System.out.println("usage: httpc get [-v] [-h key:value] URL\n"
                            + "Get executes a HTTP GET request for a given URL.\n"
                            + "-v Prints the detail of the response such as protocol, status,\n"
                            + "and headers.\n" + "-h key:value Associates headers to HTTP Request with the format\n"
                            + "'key:value'.");
                else if (splitedinput[2].equals("post"))
                    System.out.println("usage: httpc post [-v] [-h key:value] [-d inline-data] [-f file] URL\n"
                            + "Post executes a HTTP POST request for a given URL with inline data or from\n"
                            + "file.\n" + "-v Prints the detail of the response such as protocol, status,\n"
                            + "and headers.\n" + "-h key:value Associates headers to HTTP Request with the format\n"
                            + "'key:value'.\n" + "-d string Associates an inline data to the body HTTP POST request.\n"
                            + "-f file Associates the content of a file to the body HTTP POST\n" + "request.\n"
                            + "Either [-d] or [-f] can be used but not both.");
            }
        }

        else if (splitedinput[1].equals("get"))
        {
            for (int i=0;i<splitedinput.length;i++)
            {
                if (splitedinput[i].equals("-d")||splitedinput[i].equals("-f"))
                {
                    System.out.println("Unknown command. Get method can't be used with -d or -f");
                    System.exit(0);
                }
                if (splitedinput[i].equals("-h"))
                {
                    header+=splitedinput[i+1]+
                            splitedinput[i+2]+" \n";
                }
                if (splitedinput[i].equals("-v"))vFlag=true;
            }
            if (vFlag==true)getRequest(splitedinput[splitedinput.length-1], header, true);
            else
            {
                getRequest(splitedinput[splitedinput.length-1], header, false);
            }
        }
        else if (splitedinput[1].equals("post"))
        {
            for (int i=0;i<splitedinput.length;i++)
            {
                if (splitedinput[i].equals("-h"))
                {
                    header+=splitedinput[i+1]+
                            splitedinput[i+2]+" \n";
                }
                if (splitedinput[i].equals("-d"))
                {
                    body+=splitedinput[i+1]+" \n";
                }
                if (splitedinput[i].equals("-f"))
                {
                    String content = new String(Files.readAllBytes(Paths.get("file.txt")), StandardCharsets.UTF_8);
                    body+=content+" \n";
                }
            }
            if (splitedinput[2].equals("-v"))
                postRequest(splitedinput[splitedinput.length-1], header, body);

            else
                postRequest(splitedinput[splitedinput.length-1], header, body);
        }
        else
        {
            System.out.println("Unknown command: "+splitedinput[1]);
        }
    }
}