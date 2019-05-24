package entry;

import utils.Util;

import java.util.ArrayList;
import java.util.List;

public class Car {
    public static final double CAR_INIT_ENERGY = 100000;

    private double[] location;

    private List<Node> chargeQueue = new ArrayList<>();
    private List<Node> chargeQueue2 = new ArrayList<>();
    private double energy = CAR_INIT_ENERGY;

    private double moveSpeed;
    private double energyConsumRate;

    private double chargeEnergy = 0;
    private double moveEnergy = 0;

    public Car(double[] location, double moveSpeed, double energyConsumRate) {
        this.location = location;
        this.moveSpeed = moveSpeed;
        this.energyConsumRate = energyConsumRate;
    }

    //  小车移动，返回移动时间
    public boolean move(Node node) {
        //  一个时间片的移动距离
        double[] destination = node.getLocation();
        double distance = Util.distance(destination, this.location);
        if (distance>this.moveSpeed) {
            if (this.location[0]<destination[0])
                this.location[0] += (destination[0]-this.location[0])*this.moveSpeed/distance;
            else
                this.location[0] -= (this.location[0]-destination[0])*this.moveSpeed/distance;

            if (this.location[1]<destination[1])
                this.location[1] += (destination[1]-this.location[1])*this.moveSpeed/distance;
            else
                this.location[1] -= (this.location[1]-destination[1])*this.moveSpeed/distance;
            this.moveEnergy += distance*this.energyConsumRate;
            return false;
        } else {
            this.location[0] = destination[0];
            this.location[1] = destination[1];
            this.moveEnergy += distance*this.energyConsumRate;
            //  小车对节点进行充电
            this.chargeEnergy += node.charge();
            node.setResponse(false);
            return true;
        }
    }

    // 回基站补充能量
    public void initEnergy() {
        this.energy = CAR_INIT_ENERGY;
    }

    public void addTask(Node node) {
        this.chargeQueue2.add(node);
    }

    public double[] getLocation() {
        return location;
    }

    public double getChargeEnergy() {
        return chargeEnergy;
    }

    public double getMoveEnergy() {
        return moveEnergy;
    }
    public List<Node> getChargeQueue() {
        return chargeQueue;
    }

    public void setChargeQueue(List<Node> chargeQueue) {
        this.chargeQueue = chargeQueue;
    }

    public List<Node> getChargeQueue2() {
        return chargeQueue2;
    }

    public void setChargeQueue2(List<Node> chargeQueue2) {
        this.chargeQueue2 = chargeQueue2;
    }

    public double getMoveSpeed() {
        return moveSpeed;
    }
}
