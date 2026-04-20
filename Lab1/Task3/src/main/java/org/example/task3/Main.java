package org.example.task3;

public class Main {
    public static void main(String[] args) {
        SpaceScene scene = SpaceScene.variant330906();
        scene.play();

        System.out.println("Scene state: " + scene.state());
        System.out.println("Screen size: " + scene.screen().size());
        System.out.println("Visible objects: " + scene.screen().totalVisibleObjects());
        System.out.println("Edge objects: " + scene.screen().getObjectsInRegion(ScreenRegion.EDGE).size());
        System.out.println("Corner objects: " + scene.screen().getObjectsInRegion(ScreenRegion.CORNER).size());
    }
}

