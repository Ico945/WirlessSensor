package old;

import java.util.ArrayList;
import java.util.List;

public class My_idea {
    private static List<double[]> char_que = new ArrayList<double[]>();
    private double[] char_sensor;
    private double[] car_loc = {100, 100};
    private double road_energy = 0;
    private double charge_energy = 0;
    private double dead = 0;
    private double range = 1.25;


    public static void main(String[] args){
        My_idea my_idea = new My_idea();
        NJNP njnp = new NJNP();

        List<double[]> list = njnp.sensors(2000);
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
            my_idea.charge(list);
            time--;
        }

    }


    void charge(List<double[]> sensors) {     // 0.1 S 一个时间片
        if (char_sensor != null && Math.pow(char_sensor[0]-car_loc[0], 2)+  // 判断是否到达充电位置
                Math.pow(char_sensor[1]-char_sensor[1], 2) < 0.003) {

//            int i = 20;
//            while (i>0){    // 模拟充电时间 2S
//                for (double[] sensor : sensors){
//                    sensor[2] -= 0.002;
//                    if (sensor[2]<=30 && !char_que.contains(sensor))
//                        char_que.add(sensor);
//                }
//                i--;
//            }

            charge_energy += 100 - char_sensor[2];
            char_sensor[2] = 100;
            char_que.remove(char_sensor);
            for (double[] sensor : sensors){
                if (distance(char_sensor, sensor) <= range){     // 查看节点是否在多跳范围内，是的话充满（在队列中的话移出队列）
                    charge_energy += 100 - sensor[2];
                    sensor[2] = 100;
                    char_que.remove(sensor);
                }
            }
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
            double distance = 1000;   // 最长距离（对角线）
            for (double[] sensor : char_que){
                if (distance(car_loc, sensor) < distance){
                    char_sensor = sensor;
                    distance = distance(car_loc, sensor);
                }
            }

            //  得到候选锚点集合
            int max_cov = 0;
            double[] Anchor=char_sensor;
            List<double[]> Candidate_Anchors = new ArrayList<>();
            for (double[] Candidate_Anchor : sensors) {
                if (distance(char_sensor, Candidate_Anchor) <= range) {
                    Candidate_Anchors.add(Candidate_Anchor);
                }
            }

            // 锚点的选择,选择覆盖节点（待充电）最多的当选锚点
            /*
            todo 考虑两个以上节点当选锚点的时候，若覆盖数量一样的问题；解决方法：设置优先级，直接进行优先级的比较。
             */
            for (double[] s1 : Candidate_Anchors) {
                if (get_covnum(char_que, s1) > max_cov) {
                    Anchor = s1;
                    max_cov = get_covnum(sensors, s1);
                }
            }

            char_sensor = Anchor;   // 选择成功
        }


        if (char_sensor != null) {  // 表示小车往目标方向移动0.1m  1m/s
            car_loc[0] += (char_sensor[0] - car_loc[0]) / distance(char_sensor, car_loc) / 10;
            car_loc[1] += (char_sensor[1] - car_loc[1]) / distance(char_sensor, car_loc) / 10;
            road_energy += 6;
        }

        // 输出
        //old.NJNP.print_ener("D:/plan2-ener.txt", charge_energy, road_energy);

        NJNP.print_dead("D:/plan2-dead.txt", dead);
    }

    double distance(double[] location1, double[] location2){
        return Math.sqrt(Math.pow(location1[0]-location2[0], 2) + Math.pow(location1[1]-location2[1], 2));
    }

    int get_covnum(List<double[]> char_que, double[] sensor) {
        int num = 0;
        for (double[] s : char_que) {
            if (distance(s, sensor) < range)
                num++;
        }
        return num;
    }
}
