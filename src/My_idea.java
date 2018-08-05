import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class My_idea {
    private static List<double[]> char_que = new ArrayList<double[]>();
    private static double[] char_sensor;
    private static double[] car_loc = {25, 25};
    private static double road_energy = 0;
    private static double charge_energy = 0;


    public static void main(String[] args){
        My_idea my_idea = new My_idea();
        NJNP njnp = new NJNP();

        List<double[]> list = njnp.sensors(500);
        for (double[] sensor : list)
            System.out.println(sensor[0] + ";" + sensor[1] + ";" + sensor[2]);

        double[][] matrix = njnp.generating_matrix(list);
        for (double[] sub_matrix : matrix) {
            for (double x : sub_matrix)
                System.out.print(x + " ");
            System.out.println();
        }


        int time = 100000;
        while (true){
            my_idea.charge(list, --time);
        }

    }


    void charge(List<double[]> sensors, int time) {     // 0.1 S 一个时间片
        if (time < 0)
            return;
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
            for (double[] sensor : sensors){
                if (distance(char_sensor, sensor) <= 1.25){     // 查看节点是否在多跳范围内，是的话充满（在队列中的话移出队列）
                    charge_energy += 100 - sensor[2];
                    sensor[2] = 100;
                    char_que.remove(sensor);
                }
            }
            char_sensor = null;

        }
        for (double[] sensor : sensors){
            sensor[2] -= 0.002;    // 每个时间片耗电

            if (sensor[2]<=30 && !char_que.contains(sensor))
                char_que.add(sensor);
        }
        if (char_que.size()>0){     // 如果充电队列不空，小车执行充电
            double distance = 70;   // 最长距离（对角线）
            for (double[] sensor : char_que){
                if (distance(car_loc, sensor) < distance){
                    char_sensor = sensor;
                    distance = distance(car_loc, sensor);
                }
            }
        }
        if (char_sensor != null) {  // 表示小车往目标方向移动0.1m  1m/s
            car_loc[0] += (char_sensor[0] - car_loc[0]) / distance(char_sensor, car_loc) / 10;
            car_loc[1] += (char_sensor[1] - car_loc[1]) / distance(char_sensor, car_loc) / 10;
            road_energy += 3;
        }

        try {
            FileWriter fw = new FileWriter("D:/My_idea_ener.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.append(/*"小车当前位置：(" + String.format("%.2f", car_loc[0]) + "," + String.format("%.2f", car_loc[1]) + ") , 当前能量利用率：" +
                    */charge_energy/(charge_energy+road_energy) +"\r\n");
            bw.close();
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    double distance(double[] location1, double[] location2){
        return Math.sqrt(Math.pow(location1[0]-location2[0], 2) + Math.pow(location1[1]-location2[1], 2));
    }

}
