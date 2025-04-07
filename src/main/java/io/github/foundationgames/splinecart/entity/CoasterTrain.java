package io.github.foundationgames.splinecart.entity;

import java.util.ArrayList;

public class CoasterTrain {

    public final ArrayList<TrackFollowerEntity> carts = new ArrayList<>();

    public double trackVelocity;

    public CoasterTrain() {
        trackVelocity = 0;
    }

}
