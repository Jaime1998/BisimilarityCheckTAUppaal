package main;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class traceRandomNothread {

    public static synchronized void writeFile(PrintWriter out, String[] write){
        for(String w: write){
            out.print(w);
            out.print(",");
        }
            out.print("\n");
    }


    public static void main(String[] args) throws IOException, InterruptedException {
        String saveFile = "TramTraces.csv";


        FileWriter fw = new FileWriter(saveFile);
        PrintWriter out = new PrintWriter(fw, true);

        File file = new File("C:\\Users\\Jaime\\Desktop\\Train-Gate-Controller\\trainCorrectMutTau");
        final String[] pathnames = file.list();

        String pathFolder = file.getAbsolutePath();

        assert pathnames != null;
        int n = pathnames.length;
        Thread[] threadsTraces = new Thread[n];
        Thread[] threadsComparison = new Thread[((n*(n-1)))/2];


        ArrayList<String> bisimilarList = new ArrayList<>();

        HashMap<String, String> traces = new HashMap<>();

        for(int i=0; i<n; i++){
            int finalI = i;

                try{
                    String cmd = "\"C:\\Program Files\\uppaal64-4.1.25-5\\bin-Windows\\verifyta.exe\" -q -t 0 -r 0 ".concat(pathFolder).concat("\\".concat(pathnames[finalI]));
                    ProcessBuilder pb = new ProcessBuilder(cmd);
                    pb.redirectErrorStream(true);
                    Process p = null;
                    p = pb.start();

                    BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

                    String line = null;
                    String traceString = "";
                    long start = System.currentTimeMillis();

                    while ((line = stdInput.readLine()) != null) {
                        traceString.concat(line);
                    }
                    long end = System.currentTimeMillis();

                    String[] outWrite = new String[2];

                    outWrite[0] = pathnames[finalI];
                    outWrite[1] = String.valueOf(end-start);
                    writeFile(out, outWrite);

                    traces.put(pathnames[finalI], traceString);

                } catch (IOException e) {
                    e.printStackTrace();
                }

        }

        for (String pathname : pathnames) {
            System.out.println(traces.get(pathname));
        }

        int k=0;
        for(int i=0; i<n; i++){
            for(int j=i+1; j<n; j++){
                int finalI = i;
                int finalJ = j;
                threadsComparison[k++] = new Thread(()->{
                    long start = System.currentTimeMillis();
                    String traceI = traces.get(pathnames[finalI]);
                    String traceJ = traces.get(pathnames[finalJ]);
                    if(traceI.equals(traceJ)){

                        String[] outWrite = new String[5];

                        outWrite[0] = pathnames[finalI];
                        outWrite[1] = pathnames[finalJ];

                        long end = System.currentTimeMillis();
                        outWrite[2] = String.valueOf(end-start);


                        bisimilarList.add(pathnames[finalI].concat("<->").concat(pathnames[finalJ]));

                        outWrite[3] = Integer.toString(bisimilarList.size());
                        outWrite[4] = String.join("  " , bisimilarList);

                        writeFile(out, outWrite);

                        start = end;
                    }
                    else{

                        String[] outWrite = new String[3];

                        outWrite[0] = pathnames[finalI];
                        outWrite[1] = pathnames[finalJ];

                        long end = System.currentTimeMillis();
                        outWrite[2] = String.valueOf(end-start);

                        writeFile(out, outWrite);

                        start = end;
                    }
                });
            }
        }


        long start = System.currentTimeMillis();
        for (Thread thread : threadsComparison) {
            thread.start();
        }

        for (Thread thread : threadsComparison) {
            thread.join();
        }
        long end = System.currentTimeMillis();

        System.out.println(bisimilarList);
        float sec = (end - start) / 1000F;

        out.print(",");
        out.print(sec);
        out.print(",");

        //Flush the output to the file
        out.flush();

        //Close the Print Writer
        out.close();

        //Close the File Writer
        fw.close();


    }
}
