import entry.Car;
import entry.Node;
import utils.Util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Integer> deadNodes = new ArrayList<>();
        List<Double> energyUtilization = new ArrayList<>();
        List<String> information = new ArrayList<>();
        //  生成节点
        List<Node> nodes = new ArrayList<>();
        Util.generateNodes(nodes, 5000, 700);

        //  生成小车
        int carNum = 2;
        Car[] cars = new Car[carNum];
        for (int i=0; i<carNum; i++) {
            double[] carLoc = {350, 350};
            cars[i] = new Car(carLoc, 5, 0.1);
        }

        //  时间片
        int time = 0;
        int stopTime = 3600 * 120;
        int threshold = 30;
        while (time++ < stopTime) {
            //  节点消耗
            for (int i=0; i<nodes.size(); i++) {
                Node node = nodes.get(i);
                //  消耗能量
                node.setRemainEnergy(node.getRemainEnergy()-0.01);
                //  发送请求
                if (node.getRemainEnergy()<=threshold && !node.isResponse()) {
                    node.setRequestTime(time);
                    Util.distributeRequest(cars, node);
                }
            }

            //  小车删除队列中的死亡节点
            for (int i=0; i<carNum; i++) {
                Car car = cars[i];
                List<Node> removeNodes = new ArrayList<>();
                for (Node node : nodes) {
                    if (node.getRemainEnergy()<=0)
                        removeNodes.add(node);
                }
                List<Node> q1 = car.getChargeQueue();
                q1.removeAll(removeNodes);
                car.setChargeQueue(q1);

                List<Node> q2 = car.getChargeQueue2();
                q2.removeAll(removeNodes);
                car.setChargeQueue2(q2);
            }

            //  小车调度
            Util.driveCar(cars, information, nodes);
            if (deadNodes.size()==0)
                deadNodes.add(Util.deadNode(nodes));
            else
                deadNodes.add(Util.deadNode(nodes)+deadNodes.get(deadNodes.size()-1));
            energyUtilization.add(Util.energyUtilization(cars));

//            System.out.println("死亡节点：" + Util.deadNode(nodes) + ";" + "能量利用率：" + Util.energyUtilization(cars));
        }

        //  写入文件
        File fileDead = new File("./result/value(0.5*0.5)+_only_dead(2_cars,5000nodes).txt");
        File fileEnergy = new File("./result/value(0.5*0.5)+_only_energy(2_cars,5000nodes).txt");
//        File fileInformation = new File("./result/TSP_information(2_cars,6000nodes).txt");

        try {
            FileOutputStream fos1 = new FileOutputStream(fileDead);
            FileOutputStream fos2 = new FileOutputStream(fileEnergy);
//            FileOutputStream fos3 = new FileOutputStream(fileInformation);
            BufferedWriter bw1 = new BufferedWriter(new OutputStreamWriter(fos1));
            BufferedWriter bw2 = new BufferedWriter(new OutputStreamWriter(fos2));
//            BufferedWriter bw3 = new BufferedWriter(new OutputStreamWriter(fos3));

            for (int num : deadNodes)
                bw1.write(num + "\n");
//
            for (double utiliz : energyUtilization)
                bw2.write(utiliz + "\n");

//            bw3.write("# car id; round id; time; car's charge queue size\n");
//            for (String inf : information)
//                bw3.write(inf + "\n");

            bw1.flush();
            bw2.flush();
//            bw3.flush();
            bw1.close();
            bw2.close();
//            bw3.close();
        } catch (IOException e) {
            System.out.println("IO错误");
        }
    }
}
