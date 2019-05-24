package old;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class NJNP {
    private static List<double[]> char_que = new ArrayList<double[]>();
    private double[] char_sensor;
    private double[] car_loc = {25, 25};
    private double road_energy = 0;
    private double charge_energy = 0;
    private double dead = 0;


    public static void main(String[] args){
        NJNP njnp = new NJNP();

        List<double[]> list = njnp.sensors(1000);
//        for (double[] sensor : list)
//            System.out.println(sensor[0] + ";" + sensor[1] + ";" + sensor[2]);

//        double[][] matrix = njnp.generating_matrix(list);
//        for (double[] sub_matrix : matrix) {
//            for (double x : sub_matrix)
//                System.out.print(x + " ");
//            System.out.println();
//        }

        int time = 40000;
        while (time>0){
            njnp.charge(list);
            time--;
        }
    }

    List<double[]> sensors(int n){
        List<double[]> result = new ArrayList<>();
        for (int i=0; i<n; i++){
            double[] sensor = new double[3];
            sensor[0] = Double.parseDouble(String.format("%.2f", Math.random()*200));
            sensor[1] = Double.parseDouble(String.format("%.2f", Math.random()*200));
            sensor[2] = Double.parseDouble(String.format("%.2f", Math.random()*70)) + 30;
            result.add(sensor);
        }
        return result;
    }

    double[][] generating_matrix(List<double[]> sensors){
        int len = sensors.size();
        double[][] result = new double[len][len];
        for (int i=0; i<len; i++) {
            double[] sensor_1 = sensors.get(i);   // 计算距离的对比节点
            for (int j=0; j<len; j++){
                double[] sensor_2 = sensors.get(j); // 需要计算距离的节点
                result[i][j] = Double.parseDouble(String.format("%.2f",     // 计算距离，得出邻接矩阵
                        Math.sqrt(Math.pow((sensor_1[0]-sensor_2[0]), 2) + Math.pow((sensor_1[1]-sensor_2[1]), 2))));
            }
        }
        return result;
    }

    void charge(List<double[]> sensors) {     // 0.1 S 一个时间片
        if (char_sensor != null && Math.pow(char_sensor[0]-car_loc[0], 2)+  // 判断是否到达充电位置
                Math.pow(char_sensor[1]-char_sensor[1], 2) < 0.003) {

            int i = 20;
            while (i>0){    // 模拟充电时间 2S
                for (double[] sensor : sensors){
                    sensor[2] -= 0.002;
                    if (sensor[2]<=30 && !char_que.contains(sensor))
                        char_que.add(sensor);
                }
                i--;
            }

            charge_energy += 100 - char_sensor[2];
            char_sensor[2] = 100;
            char_que.remove(char_sensor);
            char_sensor = null;

        }
        for (int i=0; i<sensors.size(); i++){
            sensors.get(i)[2] -= 0.002;
            if (sensors.get(i)[2]<=30 && !char_que.contains(sensors.get(i)))
                char_que.add(sensors.get(i));
            if (sensors.get(i)[2]<=0) {
                sensors.remove(sensors.get(i));
                dead++;
            }
        }
        if (char_que.size()>0){     // 如果充电队列不空，小车执行充电
            double distance = 70;   // 最长距离（对角线）
            for (double[] sensor : char_que){
                if (Math.sqrt(Math.pow(sensor[0]-car_loc[0], 2) + Math.pow(sensor[1]-car_loc[1], 2)) < distance){
                    char_sensor = sensor;
                    distance = Math.sqrt(Math.pow(sensor[0]-car_loc[0], 2) + Math.pow(sensor[1]-car_loc[1], 2));
                }
            }
        }
        if (char_sensor != null) {  // 表示小车往目标方向移动0.1m  1m/s
            car_loc[0] += (char_sensor[0] - car_loc[0]) / Math.sqrt(Math.pow(char_sensor[1] - car_loc[1], 2) +
                    Math.pow(char_sensor[0] - car_loc[0], 2)) / 10;
            car_loc[1] += (char_sensor[1] - car_loc[1]) / Math.sqrt(Math.pow(char_sensor[1] - car_loc[1], 2) +
                    Math.pow(char_sensor[0] - car_loc[0], 2)) / 10;
            road_energy += 6;
        }

        // 输出
        print_ener("D:/NJNP_ener.txt", charge_energy, road_energy);
        //print_dead("D:/NJNP_dead.txt", dead);
    }


    // 能量利用率
    static void print_ener(String name, double charge_energy, double road_energy) {
        try {
            FileWriter fw = new FileWriter(name, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.append(/*"小车当前位置：(" + String.format("%.2f", car_loc[0]) + "," + String.format("%.2f", car_loc[1]) + ") , 当前能量利用率：" +
             */charge_energy/(charge_energy+road_energy) +"\r\n");
            bw.close();
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 输出死亡节点
    static void print_dead(String name, double dead) {
        try {
            FileWriter fw = new FileWriter(name, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.append(dead +"\r\n");
            bw.close();
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
