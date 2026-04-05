package org.example;

public class Main {
    public static void main(String[] args) {
        SpaceScene scene = SpaceScene.variant330906();

        System.out.println("Screen scale: " + scene.screen().scale());
        System.out.println("Visible objects: " + scene.events().size());
        scene.events().forEach(event ->
                System.out.println(event.order() + ". " + event.region() + " -> " + event.object().kind()));
    }
}
