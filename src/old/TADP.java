package old;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TADP {
    private static List<double[]> char_que = new ArrayList<double[]>();
    private static double[] char_sensor;
    private static double[] car_loc = {25, 25};
    private static double road_energy = 0;
    private static double charge_energy = 0;
    private static List<double[]> Pd_que = new ArrayList<>();   // 距离排序队列
    private static List<double[]> Pt_que = new ArrayList<>();   // 时间排序队列
    private double dead = 0;



    public static void main(String[] args) {
        TADP tadp = new TADP();
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
        while (time > 0) {
            tadp.charge(list);
            time--;
        }
    }


    void charge(List<double[]> sensors) {     // 0.1 S 一个时间片
        if (char_sensor != null && Math.pow(char_sensor[0] - car_loc[0], 2) +  // 判断是否到达充电位置
                Math.pow(char_sensor[1] - char_sensor[1], 2) < 0.005) {
            charge_energy += 100 - char_sensor[2];
            char_sensor[2] = 100;
            char_que.remove(char_sensor);
            Pd_que.remove(char_sensor);
            Pt_que.remove(char_sensor);
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



        if (char_que.size() > 0) {     // 如果充电队列不空，小车执行充电
            // 按照优先级对充电队列进行排序
            Collections.sort(Pd_que, new Comparator<double[]>() {
                @Override
                public int compare(double[] o1, double[] o2) {
                    if (distance(o1, car_loc)-distance(o2, car_loc)>0)
                        return -1;
                    else if (distance(o1, car_loc)-distance(o2, car_loc)<0)
                        return 1;
                    else
                        return 0;
                }
            });

            Collections.sort(char_que, new Comparator<double[]>() {
                @Override
                public int compare(double[] o1, double[] o2) {
                    if (((Pt_que.indexOf(o1)+1)*0.5+(Pd_que.indexOf(o1)+1)*0.5)
                            -((Pt_que.indexOf(o2)+1)*0.5+(Pd_que.indexOf(o2)+1)*0.5)>0)
                        return -1;
                    else if (((Pt_que.indexOf(o1)+1)*0.5+(Pd_que.indexOf(o1)+1)*0.5)
                            -((Pt_que.indexOf(o2)+1)*0.5+(Pd_que.indexOf(o2)+1)*0.5)<0)
                        return 1;
                        // 相等的情况比较乘积
                    else if ((Pt_que.indexOf(o1)+1)*(Pd_que.indexOf(o1)+1)
                            -(Pt_que.indexOf(o2)+1)*(Pd_que.indexOf(o2)+1)>0)
                        return -1;
                    else
                        return 1;
                }
            });
            char_sensor = char_que.get(0);
        }
        if (char_sensor != null) {  // 表示小车往目标方向移动0.1m  1m/s
            car_loc[0] += (char_sensor[0] - car_loc[0]) / distance(char_sensor, car_loc) / 10;
            car_loc[1] += (char_sensor[1] - car_loc[1]) / distance(char_sensor, car_loc) / 10;
            road_energy += 3;
        }

        // 输出
        NJNP.print_ener("D:/TADP_ener.txt", charge_energy, road_energy);

        //old.NJNP.print_dead("D:/TADP_dead.txt", dead);
    }

    double distance(double[] location1, double[] location2) {
        return Math.sqrt(Math.pow(location1[0] - location2[0], 2) + Math.pow(location1[1] - location2[1], 2));
    }
}

