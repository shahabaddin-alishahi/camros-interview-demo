package cameros.example.demo.run;

import cameros.example.demo.config.ConnectionPool;
import cameros.example.demo.utils.DBUtils;
import cameros.example.demo.utils.FileUtils;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.System.out;

public class ExampleByManyThread implements Runnable {

    public static void main(String[] args) {
        Date startupTime = new Date();
        DBUtils dbUtils = new DBUtils();
        FileUtils fileUtils = new FileUtils();
        List<String> list = fileUtils.readFromFile(ExampleByManyThread.class.getName());
        Runnable startupDB = () ->
        {
            dbUtils.preparedDB(ExampleByManyThread.class.getName());
        };
        AtomicInteger i = new AtomicInteger();
        Runnable callPreparedStatement = () ->
        {
            try {
                dbUtils.writingDBPreparedStatement(list.get(i.getAndAdd(1)));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        };


        new Thread(startupDB).run();
        list.parallelStream().forEach(s -> new Thread(callPreparedStatement).run());

        long runningTime = new Date().getTime() - startupTime.getTime();
        out.println("\n (ExampleByManyThread) =>  Total Partial Running time  (" + runningTime + ") => \n");

    }

    @Override
    public void run() {
    }

}
