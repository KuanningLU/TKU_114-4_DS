import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

class Student {

    private String studentId;
    private String name;
    private int score;
    private String tag;

    public Student(String studentId, String name, int score, String tag) {
        this.studentId = studentId;
        this.name = name;
        this.score = score;
        this.tag = tag;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public String getTag() {
        return tag;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "Student{" +
                "studentId='" + studentId + '\'' +
                ", name='" + name + '\'' +
                ", score=" + score +
                ", tag='" + tag + '\'' +
                '}';
    }
}

public class CourseCollectionManager {

    private List<Student> students = new ArrayList<>();

    // 保存不重複學號
    private Set<String> studentIds = new HashSet<>();

    // 學號 -> 成績
    private Map<String, Integer> scoreMap = new HashMap<>();

    // 新增學生
    public boolean addStudent(Student student) {

        if (student == null) {
            return false;
        }

        // 學號重複，不加入
        if (!studentIds.add(student.getStudentId())) {
            return false;
        }

        students.add(student);
        scoreMap.put(student.getStudentId(), student.getScore());

        return true;
    }

    // 1. 更新成績
    public boolean updateScore(String studentId, int score) {

        for (Student student : students) {

            if (student.getStudentId().equals(studentId)) {

                student.setScore(score);

                scoreMap.put(studentId, score);

                return true;
            }
        }

        return false;
    }

    // 2. 依 tag 查詢
    public List<Student> findByTag(String tag) {

        List<Student> result = new ArrayList<>();

        if (tag == null) {
            return result;
        }

        for (Student student : students) {

            String studentTag = student.getTag();

            if (studentTag != null &&
                studentTag.equalsIgnoreCase(tag)) {

                result.add(student);
            }
        }

        return result;
    }

    // 3. 成績分布
    public Map<String, Integer> scoreDistribution() {

        Map<String, Integer> distribution = new HashMap<>();

        distribution.put("A", 0);
        distribution.put("B", 0);
        distribution.put("C", 0);
        distribution.put("D", 0);
        distribution.put("F", 0);

        for (Student student : students) {

            int score = student.getScore();

            String grade;

            if (score >= 90) {
                grade = "A";
            } else if (score >= 80) {
                grade = "B";
            } else if (score >= 70) {
                grade = "C";
            } else if (score >= 60) {
                grade = "D";
            } else {
                grade = "F";
            }

            distribution.put(
                    grade,
                    distribution.get(grade) + 1
            );
        }

        return distribution;
    }

    // 4. 排名前 count 名
    public List<Student> top(int count) {

        List<Student> copy = new ArrayList<>(students);

        copy.sort(
                Comparator.comparingInt(Student::getScore)
                          .reversed()
                          .thenComparing(Student::getStudentId)
        );

        if (count >= copy.size()) {
            return copy;
        }

        if (count <= 0) {
            return new ArrayList<>();
        }

        return new ArrayList<>(copy.subList(0, count));
    }

    // 5. 移除低於 minimum 的學生
    public void removeBelow(int minimum) {

        Iterator<Student> iterator = students.iterator();

        while (iterator.hasNext()) {

            Student student = iterator.next();

            if (student.getScore() < minimum) {

                iterator.remove();

                studentIds.remove(student.getStudentId());

                scoreMap.remove(student.getStudentId());
            }
        }
    }

    public void printAll() {

        for (Student student : students) {
            System.out.println(student);
        }
    }

    public void printCollections() {

        System.out.println("List:");
        System.out.println(students);

        System.out.println("Set:");
        System.out.println(studentIds);

        System.out.println("Map:");
        System.out.println(scoreMap);
    }

    public static void main(String[] args) {

        CourseCollectionManager manager =
                new CourseCollectionManager();

        // 至少六筆資料
        System.out.println("=== 新增資料 ===");

        System.out.println(manager.addStudent(
                new Student("S001", "Amy", 95, "Java")));

        System.out.println(manager.addStudent(
                new Student("S002", "Bob", 82, "AI")));

        System.out.println(manager.addStudent(
                new Student("S003", "Tom", 82, "Java")));

        System.out.println(manager.addStudent(
                new Student("S004", "Mary", 73, "")));

        System.out.println(manager.addStudent(
                new Student("S005", "John", 65, "Database")));

        System.out.println(manager.addStudent(
                new Student("S006", "Lisa", 55, "AI")));

        // 重複學號
        System.out.println(manager.addStudent(
                new Student("S001", "Kevin", 88, "Python")));

        System.out.println();

        System.out.println("=== 原始資料 ===");
        manager.printAll();

        // 1. updateScore
        System.out.println();
        System.out.println("=== updateScore ===");

        boolean updateResult =
                manager.updateScore("S005", 88);

        System.out.println("更新 S005: " + updateResult);

        manager.printAll();

        // 2. findByTag
        System.out.println();
        System.out.println("=== findByTag Java ===");

        List<Student> javaStudents =
                manager.findByTag("Java");

        for (Student student : javaStudents) {
            System.out.println(student);
        }

        // 3. scoreDistribution
        System.out.println();
        System.out.println("=== scoreDistribution ===");

        Map<String, Integer> distribution =
                manager.scoreDistribution();

        System.out.println(distribution);

        // 4. top
        System.out.println();
        System.out.println("=== Top 3 ===");

        List<Student> topStudents =
                manager.top(3);

        for (Student student : topStudents) {
            System.out.println(student);
        }

        // count 大於人數
        System.out.println();
        System.out.println("=== Top 100 ===");

        for (Student student : manager.top(100)) {
            System.out.println(student);
        }

        // 5. removeBelow
        System.out.println();
        System.out.println("=== removeBelow 70 ===");

        manager.removeBelow(70);

        manager.printAll();

        // 確認 List、Set、Map 一致
        System.out.println();
        System.out.println("=== List / Set / Map ===");

        manager.printCollections();
    }
}