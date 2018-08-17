package sample.utils;


import javafx.scene.Node;
import sample.objects.GameObject;

import java.util.List;
import java.util.stream.Collectors;

public class Helper {


    public static double round(double d)
    {
        int temp = (int)(d * Math.pow(10 , 4));
        return ((double)temp)/Math.pow(10 , 4);
    }

    public static List<Node> gameObjectList2NodeList(List<GameObject> objects) {
        return objects.stream()
                .map(GameObject::getView)
                .collect(Collectors.toList());
    }
}
