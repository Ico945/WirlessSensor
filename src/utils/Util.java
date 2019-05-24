package utils;

import entry.Car;
import entry.Node;

import java.util.*;

public class Util {
    //  充电车充电轮次记录
    static int[] rounds = new int[4];
    //  生成随机抛洒节点地图
    public static void generateNodes (List<Node> nodes, int n, int L) {
        for (int i=0; i<n; i++) {
            double[] location = new double[2];
            location[0] = Double.parseDouble(String.format("%.2f", Math.random()*L));
            location[1] = Double.parseDouble(String.format("%.2f", Math.random()*L));
            nodes.add(new Node(location, Double.parseDouble(String.format("%.2f", Math.random()*60)) + 40));
        }
    }

    //  统计死亡节点数
    public static int deadNode(List<Node> nodes) {
        int result = 0;
        List<Node> removedNodes =  new ArrayList<>();
        for (Node node : nodes) {
            if (node.getRemainEnergy()<=0) {
                removedNodes.add(node);
                result++;
            }
        }
        nodes.removeAll(removedNodes);
        return result;
    }

    //  统计总能量利用率
    public static double energyUtilization(Car[] cars) {
        double chargeEnergy = 0;
        double moveEnergy = 0;
        for (Car car : cars) {
            chargeEnergy += car.getChargeEnergy();
            moveEnergy += car.getMoveEnergy();
        }
        return Double.parseDouble(String.format("%.2f", chargeEnergy/(chargeEnergy+moveEnergy)));
    }

    //  小车分发
    public static void distributeRequest(Car[] cars, Node node) {
        Car result = null;
        double min = 10000;
        for (int i=0; i<cars.length; i++) {
            Car car = cars[i];
            //  todo 任务分发策略，可改变。
            if (distance(car.getLocation(), node.getLocation())<min) {
                min = distance(car.getLocation(), node.getLocation());
                result = car;
            }
        }
        if (result!=null) {
            node.setResponse(true);
            result.addTask(node);
        }
    }

    //  小车调度
    public static void driveCar(Car[] cars, List<String> information, List<Node> allNodes) {
        for (int i=0; i<cars.length; i++) {
            Car car = cars[i];
            if (car.getChargeQueue().size()==0) {
                startNew(car, 1, allNodes);
                rounds[i]++;
                if (car.getChargeQueue().size()!=0)
                    information.add(""+car.getChargeQueue().size());
            }
            List<Node> q = car.getChargeQueue();
            if (q.size()>0 && car.move(q.get(0))) {
                q.get(0).setRequestTime(0);
                q.remove(0);
                car.setChargeQueue(q);
            }
        }
    }

    // 小车当前任务执行完成，下一任务队列启动
    static void startNew(Car car, double increaseThreshold, List<Node> allNodes) {

        //  先到先服务
        List<Node> chargeQueue2 = car.getChargeQueue2();
//        chargeQueue2.sort(Comparator.comparing(Node::getRequestTime));
//        car.setChargeQueue(chargeQueue2);
//
//        HashMap<Node, Integer> timeMap = new HashMap<>();
//        for (Node node : chargeQueue2) {
//            timeMap.put(node, chargeQueue2.indexOf(node)+1);
//        }

        //  TSP
        List<Node> cq = new ArrayList<>();
        double[] loc = car.getLocation().clone();
        while (!chargeQueue2.isEmpty()) {
            Node node = null;
            double minDistance = Integer.MAX_VALUE;
            for (Node n : chargeQueue2) {
                if (minDistance>Util.distance(n.getLocation(), loc)) {
                    minDistance = Util.distance(n.getLocation(), loc);
                    node = n;
                }
            }
            loc = node.getLocation().clone();
            cq.add(node);
            chargeQueue2.remove(node);
        }
        //  删除低效节点，提前下一轮响应时间
//        int index = 1;
//        double increase;
//        while (index<cq.size()-1) {
//            increase = Util.distance(cq.get(index-1).getLocation(), cq.get(index).getLocation()) +
//                    Util.distance(cq.get(index+1).getLocation(), cq.get(index).getLocation()) -
//                    Util.distance(cq.get(index-1).getLocation(), cq.get(index+1).getLocation());
//            if (increase>increaseThreshold) {
//                cq.get(index).setResponse(false);
//                cq.remove(index);
//                index = 1;
//            } else
//                index++;
//        }

        //  增加高效节点，加入充电序列
        List<Node> cq2 = new ArrayList<>(cq);
        for (int i=0; i<cq.size()-1; i++) {
            for (Node node : allNodes) {
                if (!node.getResponse()) {
                    double d1 = Util.distance(cq.get(i).getLocation(), node.getLocation());
                    double d2 = Util.distance(cq.get(i+1).getLocation(), node.getLocation());
                    double d3 = Util.distance(cq.get(i).getLocation(), cq.get(i+1).getLocation());
                    if ( Math.pow(d1, 2)+Math.pow(d2, 2)<Math.pow(d3, 2) && d1+d2-d3<increaseThreshold) {
                        node.setResponse(true);
                        cq2.add(node);
                    }
                }
            }
        }
        //  重新生成TSP
        cq = new ArrayList<>();
        loc = car.getLocation().clone();
        while (!cq2.isEmpty()) {
            Node node = null;
            double minDistance = Integer.MAX_VALUE;
            for (Node n : cq2) {
                if (minDistance>Util.distance(n.getLocation(), loc)) {
                    minDistance = Util.distance(n.getLocation(), loc);
                    node = n;
                }
            }
            loc = node.getLocation().clone();
            cq.add(node);
            cq2.remove(node);
        }

        car.setChargeQueue(cq);


        //  简单加权
//        for (int i=0; i<cq.size(); i++) {
//            Node node = cq.get(i);
//            node.setWeight(0.5*(i+1) + 0.5*timeMap.get(node));
//        }
//        cq.sort(Comparator.comparing(Node::getWeight));
//        car.setChargeQueue(cq);


        car.setChargeQueue2(new ArrayList<>());
    }

    public static double distance(double[] loc1, double[] loc2) {
        return Math.sqrt(Math.pow(loc1[0]-loc2[0], 2)+Math.pow(loc1[1]-loc2[1], 2));
    }

    //  判断是否会有死亡节点，返回死亡节点下标。没有-1
    private static int isDead(List<Node> cq, Car car) {
        double[] loc = car.getLocation().clone();
        int time = 0;
        for (Node node : cq) {
            time += (int)(distance(loc, node.getLocation())/car.getMoveSpeed())+1;
            if (node.getRemainEnergy()<time*0.01)
                return cq.indexOf(node);
            loc[0] = node.getLocation()[0];
            loc[1] = node.getLocation()[1];
        }
        return -1;
    }
}
