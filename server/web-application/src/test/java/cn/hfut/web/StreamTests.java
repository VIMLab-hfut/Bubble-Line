package cn.hfut.web;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamTests{
    protected Logger log = LoggerFactory.getLogger(this.getClass());

    private Long time;

    @Before
    public void setUp() {
        this.time = System.currentTimeMillis();
        log.info("==============> 测试开始执行 <==============>");
    }

    @After
    public void tearDown() {
        log.info("==============>> 测试执行完成，耗时：{} ms <==============>", System.currentTimeMillis() - this.time);
    }

    @Test
    public  void testStream() {
        Stream<Integer> stream = Stream.of(12, 14, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        // 过滤出偶数
        Stream<Integer> filterStream = stream
                //过滤掉奇数，只取偶数
                .filter(integer -> integer % 2 == 0)
                //排序
                .sorted()
                //取前4个
                .limit(4)
                //跳过前两个
                .skip(2);
        // 简单输出
        filterStream.forEach(System.out::println);

        //测试collect方法
        List<String> list = Arrays.asList("java", "ios", "c" , "python");
        String ret = list.stream().collect(Collectors.joining(":", "<", ">"));
        System.out.println(ret);//@@java:ios:c@@
    }

    @Test
    public void testFunctionInterface(){
        String[] arr = {"迪丽热巴,女", "古力娜扎,女", "马尔扎哈,男"};
        printInfo(s -> System.out.print("姓名:"+ s.split(",")[0]+"。"), s -> System.out.println("性别："+s.split(",")[1]+"。"), arr);
    }

    public  void printInfo(Consumer<String> one, Consumer<String> two, String[] data) {
        for (String s : data){
            one.andThen(two).accept(s);
        }
    }

    @Test
    public  void collectGroupingBy() {
        List<Student> list = Arrays.asList(new Student("吴六", 26), new Student("张三", 26), new Student("李四", 27));
        Map<Integer, List<Student>> ret = list.stream().collect(Collectors.groupingBy(
                Student::getAge,
                (Supplier<Map<Integer, List<Student>>>) HashMap::new,
                Collectors.toList()));

        for (Map.Entry<Integer, List<Student>> entry : ret.entrySet()) {
            Integer mapKey = entry.getKey();
            List<Student> mapValue = entry.getValue();
            System.out.println(mapKey + ":" + mapValue);
        }

    }


    private class Student {
        private String name;
        private int age;

        public Student(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }
}
