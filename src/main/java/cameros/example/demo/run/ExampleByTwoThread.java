package cameros.example.demo.run;

import cameros.example.demo.utils.DBUtils;
import cameros.example.demo.utils.FileUtils;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.System.out;

public class ExampleByTwoThread implements Runnable {

    public static void main(String[] args) {
        Date startupTime = new Date();
        DBUtils dbUtils = new DBUtils();
        FileUtils fileUtils = new FileUtils();
        List<String> list = fileUtils.readFromFile(ExampleByManyThread.class.getName());
        Runnable startupDB = () ->
        {
            dbUtils.preparedDB(ExampleByManyThread.class.getName());
        };

        Thread dbThread = new Thread(startupDB);

        dbThread.start();

        list.forEach(s -> {
            try {
                dbUtils.writingDBPreparedStatement(s);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        Long runningTime = new Date().getTime() - startupTime.getTime();
        out.println("\n (ExampleByTwoThread) =>  Total Partial Running time  (" + runningTime + ") => \n");

    }

    @Override
    public void run() {
    }

}
