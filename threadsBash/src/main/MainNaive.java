package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;


public class MainNaive {

    public static void main(String[] args) throws IOException, InterruptedException {
        File file = new File("C:\\Users\\Jaime\\Desktop\\tramBisim2");
        final String[] pathnames = file.list();

        String pathFolder = file.getAbsolutePath();

        assert pathnames != null;
        int n = pathnames.length;

        long start = System.currentTimeMillis();
        for(int i=0; i<n; i++) {
            for (int j = i + 1; j < n; j++) {
                try {
                    String cmd = "\"C:\\Program Files (x86)\\Common Files\\Oracle\\Java\\javapath\\java.exe\" -Djava.library.path=PATH\\x64_win64 -cp \"tool.jar;*;PATH\\cplex.jar\" main.Main -b 30 ".concat(pathFolder).concat("\\".concat(pathnames[i])).concat(" ").concat(pathFolder).concat("\\".concat(pathnames[j])).concat("\"");

                    //Process p = Runtime.getRuntime().exec(cmd);

                    ProcessBuilder pb = new ProcessBuilder(cmd);
                    pb.redirectErrorStream(true);
                    Process p = null;
                    p = pb.start();

                    BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

                    String line = null;
                    while ((line = stdInput.readLine()) != null) {
                        if(line.contains("Result of bisimulation check: true")){
                            System.out.println(pathnames[i] + " - " + pathnames[j]);
                        }
                    }

                    p.waitFor();

                }catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }
        }

        long end = System.currentTimeMillis();

        float sec = (end - start) / 1000F;
        System.out.println(sec + " seconds");

    }
}
