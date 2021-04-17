public class test{


    private String name;
    private String address;

    static class innner{
        public void print(){
            System.out.println("OK");
        }
    }


    public test(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public static void main(String[] args) {
        test tes = new test("1", "2");
        test.innner in = new test.innner();
        in.print();
    }
}
