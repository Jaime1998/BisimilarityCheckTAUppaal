package main;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/*

new Thread(() -> {
                System.out.println("threads");
            }).start();
 */
/*
            java -Djava.library.path=PATH\x64_win64 -cp "tool.jar;*;PATH\cplex.jar" main.Main -b 30 "$folderMutants"/"${arrayMutants[$i]}"  "$folderMutants"/"${arrayMutants[$j]}




        for (int i = 0; i < 2; i++) {

            String cmd = "\"C:\\Program Files (x86)\\Common Files\\Oracle\\Java\\javapath\\java.exe\" -Djava.library.path=PATH\\x64_win64 -cp \"tool.jar;*;PATH\\cplex.jar\" main.Main -b 30 \"C:\\Users\\Jaime\\Desktop\\tramBisim2\\tmi2.xml\" \"C:\\Users\\Jaime\\Desktop\\tramBisim2\\tmi2.xml\"";

            //Process p = Runtime.getRuntime().exec(cmd);

            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.redirectErrorStream(true);
            Process p = pb.start();

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

            System.out.println("Here is the standard input of the command:\n");
            String line = null;
            while ((line = stdInput.readLine()) != null) {
                System.out.println(line);
            }

            p.waitFor();
        }
 */
public class Main {

    public static synchronized void writeFile(PrintWriter out, String[] write){

        for(String w: write){
            out.print(w);
            out.print(",");
        }
        out.print("\n");
    }


    public static void main(String[] args) throws IOException, InterruptedException {

        String saveFile = "TramBisim2.csv";


        FileWriter fw = new FileWriter(saveFile);
        PrintWriter out = new PrintWriter(fw, true);

        File file = new File("C:\\Users\\Jaime\\Desktop\\tramBisim2");
        final String[] pathnames = file.list();

        String pathFolder = file.getAbsolutePath();

        assert pathnames != null;
        int n = pathnames.length;
        Thread[] threads = new Thread[((n*(n-1))/2)];

        ArrayList<String> bisimilarList = new ArrayList<>();

        int k = 0;
        for(int i=0; i<n; i++){
            for(int j=i+1; j<n; j++){
                final int finalI = i;
                final int finalJ = j;
                threads[k++] = new Thread(()->{
                    try {
                        String cmd = "\"C:\\Program Files (x86)\\Common Files\\Oracle\\Java\\javapath\\java.exe\" -Djava.library.path=PATH\\x64_win64 -cp \"tool.jar;*;PATH\\cplex.jar\" main.Main -b 30 ".concat(pathFolder).concat("\\".concat(pathnames[finalI])).concat(" ").concat(pathFolder).concat("\\".concat(pathnames[finalJ])).concat("\"");

                        //Process p = Runtime.getRuntime().exec(cmd);

                        ProcessBuilder pb = new ProcessBuilder(cmd);
                        pb.redirectErrorStream(true);
                        Process p = null;
                        p = pb.start();

                        BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

                        String line = null;
                        long start = System.currentTimeMillis();

                        while ((line = stdInput.readLine()) != null) {

                            if(line.contains("Result of bisimulation check: true")){

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

                            }if(line.contains("Result of bisimulation check: false")){

                                String[] outWrite = new String[3];

                                outWrite[0] = pathnames[finalI];
                                outWrite[1] = pathnames[finalJ];

                                long end = System.currentTimeMillis();
                                outWrite[2] = String.valueOf(end-start);

                                writeFile(out, outWrite);

                                start = end;
                            }
                        }
                        p.waitFor();
                    }catch (InterruptedException | IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        }

        long start = System.currentTimeMillis();
        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }
        long end = System.currentTimeMillis();

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
