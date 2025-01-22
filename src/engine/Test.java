package engine;

import java.util.ArrayList;
import java.util.Arrays;
public class Test {

    public static ArrayList<int[]> genSequence(int[] max){
    	ArrayList<int[]> a = new ArrayList<>();
    	genSequence(a,new int[max.length],max);
    	return a;
    }

    public static void genSequence(ArrayList<int[]> a, int[] curr, int[] max){
    	a.add(curr.clone());
    	if(Arrays.equals(curr,max))
    		return;
    	int index = curr.length-1;
    	curr[index]++;
    	while(index>=0 && curr[index]>max[index]){
    		curr[index] = 0;
    		index--;
    		if(index>=0)
    			curr[index]++;
    	}
    	genSequence(a,curr,max);
    }

    public static void main(String[] args){
    	ArrayList<int[]> nums = genSequence(new int[]{1,1,1,1,1,0,0,0,0,0,0,0,3});
    	for(int[] a : nums){
    		System.out.println(Arrays.toString(a));
    	}
    }

}