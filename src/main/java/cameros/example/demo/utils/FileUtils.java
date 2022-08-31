package cameros.example.demo.utils;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import static java.lang.System.out;

public class FileUtils {


    public List<String> readFromFile(String callerName) {
        Date startupTime = new Date();
        List<String> list = new ArrayList<>();
        try {
            Resource resource = new ClassPathResource("/Demo.txt");
            FileInputStream fis = new FileInputStream(resource.getFile());
            Scanner sc = new Scanner(fis);
            while (sc.hasNextLine()) {
                list.add(sc.nextLine());
            }
            sc.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Long runningTime = new Date().getTime() - startupTime.getTime();
            out.println("\n " + callerName + " (DBUtils) => Total Running time for reading lines from file  (" + runningTime + ") => \n");
            return list;
        }
    }
}
