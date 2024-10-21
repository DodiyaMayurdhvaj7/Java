import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;
import javax.swing.JOptionPane;
class ATM{
    public static void main(String[] args)throws Exception {
        String Driver="com.mysql.cj.jdbc.Driver"; //load driver
        Class.forName(Driver);
        System.out.println("Driver Load Successfully");
        String dburl="jdbc:mysql://localhost:3306/ customer_details";
        String dbuser="root";
        String dbpass="";
        Connection con=DriverManager.getConnection(dburl, dbuser, dbpass);   //connection with data base
        if(con!=null){
            System.out.println("Connection Success");
        }
        else{
            System.out.println("Connection Faild");
        }
        Scanner sc=new Scanner(System.in);
        PreparedStatement pst;      //Declaration of variables
        CallableStatement cst;
        File f;
        FileWriter fw=null;
        BufferedWriter bw=null;
        ResultSet rs;
        String sql;
        int Pin,PIN1,PIN2;
        boolean C_P_found;
        Print p=new Print();
        p.Welcome();
        Thread.sleep(1500);
        // p.donotremove();
        // Thread.sleep(1500);
        try{
        while(true){
            p.mainmenu();
            System.out.print("\t\tEnter Your Choice : ");
            String ch = sc.next();             //Users Enter choice hear
            Thread.sleep(1000);       //Thread for slow process
            switch (ch){
                case "1":{
                    String ch1;
                    do{
                        p.bankimgmenu();      //print banking Menu
                        System.out.print("\t\tEnter Your Choice : ");
                        ch1=sc.next();         //User enter choice Hear
                        Thread.sleep(500);
                        switch(ch1){
                            case "1":{
                            sql="Update customer set c_Pin=? where c_Pin=?";   //sql query for update pin
                            pst=con.prepareStatement(sql);     
                            System.out.print("\t\tEnter Your Old Pin : ");
                            Pin=sc.nextInt();
                            System.out.println("\n====================== Change PIN ====================");
                            C_P_found = false;
                            while (C_P_found == false){
                                System.out.print("\t\tEnter New PIN : ");
                                PIN1 = sc.nextInt();
                                System.out.print("\t\tConform New PIN : ");
                                PIN2 = sc.nextInt();
                                if (PIN1 == PIN2 && PIN1 >= 1000 && PIN1 <= 9999){
                                    C_P_found = true;
                                    pst.setInt(2, Pin);
                                    pst.setInt(1, PIN1);
                                    int r=pst.executeUpdate();
                                    if(r>0){
                                        p.pinchange();    //print pin change sucesfully
                                    }
                                    else{
                                        System.out.println("\u001B[31m"+"\n\t\t-------------------!!!\n\t\tInvalid Old-Pin..!!\n\t\t-------------------!!!"+"\u001B[0m");
                                    }
                                }
                                else{
                                    C_P_found = false;
                                    p.pinchangefail();    //print pin change fail
                                }
                            }
                            Thread.sleep(500);
                            break;
                            }
                            case "2":{
                                String ch2;
                                do{
                                    p.accounttype();    //print accout type
                                    System.out.print("\t\tEnter Account Type : ");
                                    ch2=sc.next();
                                    String acctype="";
                                    switch(ch2){
                                        case "1":{
                                            acctype="Current";
                                            sql="call withdrawmoneycurrentaccount(?,?,?)";  //sql query for withdrawmoney using callable statement
                                            cst=con.prepareCall(sql);
                                            System.out.print("\t\tEnter Pin : ");
                                            Pin=sc.nextInt();
                                            System.out.print("\t\tEnter Amount (Between 100-50000 Limit) : ");
                                            double amount=sc.nextDouble();
                                        if(amount<=50000 && amount >=100)
                                            {
                                                cst.setInt(1, Pin);
                                                cst.setDouble(2,amount);
                                                cst.setString(3,acctype);
                                                rs=cst.executeQuery();
                                                int flag=0;
                                                while(rs.next()){
                                                      double amount1=rs.getDouble(5);
                                                if(amount<amount1){
                                                flag++;
                                                p.Transactioncomplete(rs);
                                                long acc=rs.getLong(4);
                                                f=new File(acc+".txt");                
                                                fw=new FileWriter(f,true);         //create new text files
                                                bw=new BufferedWriter(fw);
                                                bw.write("withdraw amount ="+amount);
                                                bw.newLine();
                                                bw.flush();
                                                }
                                                else{
                                                    p.Insufficientbalance();   //Display not enough balance
                                                }
                                            }
                                                if(flag==0){
                                                    System.out.println("\u001B[31m"+"\t\t-------------------!!!\n\t\tInvalid Pin..!!\n\t\t-------------------!!!"+"\u001B[0m");
                                                }
                                                ch2="3";
                                            }
                                            else{
                                                System.out.println("\u001B[31m"+"\t\t-------------------!!!\n\t\tTransaction Failed\n\t\tEnter Amount As Per Your Limit..!!\n\t\t-------------------!!!"+"\u001B[0m");
                                                ch2="3";
                                            }
                                            Thread.sleep(500);
                                            break;

                                        }
                                        case "2":{
                                            acctype="Saving";
                                            sql="call withdrawmoneysavingaccount(?,?,?)";   //Query for withdraw money from saving account 
                                            cst=con.prepareCall(sql);
                                            System.out.print("\t\tEnter Pin : ");
                                            Pin=sc.nextInt();
                                            System.out.print("\t\tEnter Amount (Between 100-20000 Limit) : ");   //limit is 100-20000
                                            double amount=sc.nextDouble();
                                            if(amount<=20000 && amount >=100){
                                                cst.setInt(1, Pin);
                                                cst.setDouble(2,amount);
                                                cst.setString(3,acctype);
                                                rs=cst.executeQuery();
                                                int flag=0;
                                                while(rs.next()){
                                                     double amount1=rs.getDouble(5);
                                                if(amount<amount1){
                                                    flag++;
                                                    p.Transactioncomplete(rs);
                                                    long acc=rs.getLong(4);
                                                    f=new File(acc+".txt");
                                                    fw=new FileWriter(f,true);      //create new txt files
                                                    bw=new BufferedWriter(fw);
                                                    bw.write("withdraw amount ="+amount);
                                                    bw.newLine();
                                                    bw.flush();
                                                }
                                                else{
                                                    p.Insufficientbalance();   //display not enough balance
                                                }
                                            }
                                                if(flag==0){
                                                    System.out.println("\u001B[31m"+"\t\t-------------------!!!\n\t\tInvalid Pin..!!\n\t\t-------------------!!!"+"\u001B[0m");
                                                }
                                                ch2="3";
                                                
                                            }
                                            else{
                                                System.out.println("\u001B[31m"+"\t\t-------------------!!!\n\t\tTransaction Failed\n\t\tEnter Amount As Per Your Limit..!!\n\t\t-------------------!!!"+"\u001B[0m");
                                                ch2="3";
                                            }
                                            Thread.sleep(500);
                                            break;
                                        }
                                        default:{
                                        System.out.println("\u001B[31m"+"\t\t-------------------!!!\n\t\tInvalid Choice..!!\n\t\t-------------------!!!"+"\u001B[0m");
                                        break;
                                        }
                                    }    
                                }while(!ch2.equalsIgnoreCase("3"));
                                break;
                            }
                            case "3":{
                                String ch3;
                                p.accounttype();
                                System.out.print("\t\tEnter Account Type : ");
                                ch3=sc.next();
                                String acctype="";
                                switch(ch3){
                                    case "1":{
                                        acctype="Current";
                                        sql="call Depositmoneycurrentaccount(?,?,?)";   //deposite money using callable
                                        cst=con.prepareCall(sql);
                                        System.out.print("\t\tEnter Pin : ");
                                        Pin=sc.nextInt();
                                        System.out.print("\t\tEnter Amount Between 0-50000 Limit : ");
                                        double amount=sc.nextDouble();
                                        cst.setInt(1, Pin);
                                        cst.setDouble(2,amount);
                                        cst.setString(3,acctype);
                                        rs=cst.executeQuery();
                                        if(amount<=50000){
                                            int flag=0;
                                            while(rs.next()){
                                                flag++;
                                                p.Transactioncomplete(rs);
                                                long acc=rs.getLong(4);
                                                f=new File(acc+".txt");
                                                fw=new FileWriter(f,true);    
                                                bw=new BufferedWriter(fw);
                                                bw.write("Deposit money ="+amount);
                                                bw.newLine();
                                                bw.flush();
                                            }
                                            if(flag==0){
                                                System.out.println("\u001B[31m"+"\t\t-------------------!!!\n\t\tInvalid Pin..!!\n\t\t-------------------!!!"+"\u001B[0m");
                                            }
                                            ch3="3";
                                        }
                                        else{
                                            System.out.println("\u001B[31m"+"\t\t-------------------!!!\n\t\tTransaction Failed\n\t\tEnter Amount As Per Your Limit..!!\t\t-------------------!!!"+"\u001B[0m");
                                            ch3="3";
                                        }
                                        Thread.sleep(500);
                                        break;
                                    }
                                    case "2":{
                                        acctype="Saving";
                                        sql="call Depositmoneysavingaccount(?,?,?)";
                                        cst=con.prepareCall(sql);
                                        System.out.print("\t\tEnter Pin : ");
                                        Pin=sc.nextInt();
                                        System.out.print("\t\tEnter Amount (Between 0-20000 Limit) : ");
                                        double amount=sc.nextDouble();
                                        cst.setInt(1, Pin);
                                        cst.setDouble(2,amount);
                                        cst.setString(3,acctype);
                                        rs=cst.executeQuery();
                                        if(amount<=20000){
                                            int flag=0;
                                            while(rs.next()){
                                                flag++;
                                                p.Transactioncomplete(rs);
                                                long acc=rs.getLong(4);
                                                f=new File(acc+".txt");
                                                fw=new FileWriter(f,true);
                                                bw=new BufferedWriter(fw);
                                                bw.write("Deposit money ="+amount);
                                                bw.newLine();
                                                bw.flush();                        
                                            }
                                            if(flag==0){
                                            System.out.println("\u001B[31m"+"\t\t-------------------!!!\n\t\tInvalid Pin..!!\n\t\t-------------------!!!"+"\u001B[0m");
                                            }
                                            ch3="3";
                                        }
                                        else{
                                            System.out.println("\u001B[31m"+"\t\t-------------------!!!\n\t\tTransaction Failed\n\t\tEnter Amount As Per Your Limit..!!\n\t\t-------------------!!!"+"\u001B[0m");
                                            ch3="3";
                                        }
                                        Thread.sleep(500);
                                        break;
                                    }
                                    case "3":{
                                        System.out.println("\u001B[31m"+"\t\t-------------------!!!\n\t\tInvalid Choice..!!\n\t\t-------------------!!!"+"\u001B[0m");
                                        break;
                                    }    
                                }while(!ch3.equalsIgnoreCase("3"));
                                Thread.sleep(500);
                            break;
                            }
                            case "4":{
                                int Otp =(int)(Math.random()*10000);     //Genrate 4 digit random number as a otp
                                System.out.print("\t\tEnter Your Account Number : ");
                                Long acc=sc.nextLong();
                                p.otpsend();   //display otp send on your account number
                                String name="Your Atm Pin Generation OTP is ";
                                JOptionPane.showMessageDialog(null,name + Otp);  // showmessagedialog method name
                                System.out.print("\t\tEnter OTP : ");
                                int otp1=sc.nextInt();
                                if(Otp==otp1){
                                    sql="Update customer set c_Pin=? where c_Pin=? and c_Accnumber=?";
                                    pst=con.prepareStatement(sql);
                                    System.out.println("\n============= Change PIN =============\n");
                                    C_P_found = false;
                                    while (C_P_found == false) {
                                        System.out.print("\t\tEnter New PIN : ");
                                        PIN1 = sc.nextInt();
                                        System.out.print("\t\tConfirm New PIN : ");
                                        PIN2 = sc.nextInt();
                                        if (PIN1 == PIN2 && PIN1 >= 1000 && PIN1 <= 9999) {
                                            C_P_found = true;
                                            pst.setInt(1, PIN1);
                                            pst.setInt(2,0);
                                            pst.setLong(3,acc);
                                            int r=pst.executeUpdate();
                                            if(r>0){
                                                p.pinchange();
                                            }
                                            else{
                                                System.out.println("\u001B[31m"+"\t\t-------------------!!!\n\t\tPin Already Generated..!!\n\t\t-------------------!!!\n\n"+"\u001B[0m");
                                            }
                                        }
                                    }
                                }
                                else{
                                    p.invalidotp();     //print invalid otp
                                }
                                Thread.sleep(500);
                            break;
                            }
                            case "5":{
                                //Thread.sleep(500);
                                break;
                            }
                            case "6":{
                                p.visiting();      //print thanks for visiting
                                System.exit(0);
                            }
                            default:{
                                System.out.println("\u001B[31m"+"\t\t-------------------!!!\n\t\tInvalid Choice..!!\n\t\t-------------------!!!\n\n"+"\u001B[0m");
                            Thread.sleep(1000);
                            break;
                            }
                        }
                    }while(!ch1.equalsIgnoreCase("5"));
                    Thread.sleep(1000);
                    break;
                }
                case "2":{
                    sql="call checkbalance(?)";     //sql query for display account balance
                    cst=con.prepareCall(sql);
                    System.out.print("\t\tEnter Pin : ");
                    Pin=sc.nextInt();
                    cst.setInt(1, Pin);
                    rs=cst.executeQuery();
                    int flag=0;
                    while(rs.next()){
                        flag++;
                    p.Balance(rs);
                    }
                    if(flag==0){
                        System.out.println("\u001B[31m"+"\t\t-------------------!!!\n\t\tInvalid Pin..!!\n\t\t-------------------!!!\n\n"+"\u001B[0m");
                    }
                    Thread.sleep(500);
                break;
                }
                case "3":{
                    sql="call Fundtransfer(?,?,?)";    //Query for fund transfer
                    String sql3="select c_Accnumber,c_Name from customer where c_Accnumber=?";
                    String sql2="Select c_Accbalance from customer where c_Pin=?";
                    pst=con.prepareStatement(sql3);
                    PreparedStatement pst1=con.prepareStatement(sql2);
                    System.out.print("\t\tEnter Pin : ");
                    Pin=sc.nextInt();
                    System.out.print("\t\tEnter 12 Digit Account number : ");
                    long accno=sc.nextLong();
                    System.out.print("\t\tEnter Amount : ");
                    double amount=sc.nextDouble();
                    cst=con.prepareCall(sql);
                    cst.setInt(1, Pin);
                    cst.setDouble(2,amount);
                    cst.setLong(3,accno);
                    pst.setLong(1,accno);
                    pst1.setInt(1, Pin);
                    rs=cst.executeQuery();
                    ResultSet rs1=pst.executeQuery();
                    ResultSet rs2=pst1.executeQuery();
                    if(rs1.next()){
                        if(rs2.next()){
                            double c_Accbalance=rs2.getDouble(1);
                            if(amount<c_Accbalance){       
                                while(rs.next()){
                                    p.Transactioncomplete(rs);
                                    long acc=rs.getLong(4);
                                    f=new File(acc+".txt");
                                    fw=new FileWriter(f,true);
                                    bw=new BufferedWriter(fw);
                                    bw.write("Transfer amount ="+amount+" to "+accno);
                                    bw.newLine();
                                    bw.flush();
                                    fw.flush();
                                    File f1=new File(accno+".txt");
                                    FileWriter fw1=new FileWriter(f1,true);
                                    BufferedWriter bw1=new BufferedWriter(fw1);
                                    bw1.write("Recive amount ="+amount+" from "+acc);
                                    bw1.newLine();
                                    bw1.flush();
                                    bw1.close();
                                }
                            } 
                            else{
                                p.Insufficientbalance();
                            }
                        }
                        else{
                            p.invalidpin();
                        }
                    }
                    else{
                        p.invalidaccountnumber();
                    }
                    Thread.sleep(500);
                break;
                }
                case "4":{
                    sql="call checkbalance(?)";
                    cst=con.prepareCall(sql);
                    System.out.print("\t\tEnter Pin : ");
                    Pin=sc.nextInt();
                    cst.setInt(1, Pin);
                    rs=cst.executeQuery();
                    int flag=0;
                    while(rs.next()){
                        flag++;
                        p.Collectrecipt();
                        System.out.println("\t\t$$$--------------------------------$$$");    
                        System.out.println("Account Number:"+rs.getLong(4));
                        System.out.println("Account Holder Name:"+rs.getString(2));
                        System.out.println("Account Holder Phone number:"+rs.getLong(7));
                        System.out.println("Account Type:"+rs.getString(6));
                        //System.out.println("Account Holder Pin:"+temp.account.Account_pin);
                        System.out.println("Account Balance:"+rs.getDouble(5));
                        System.out.println("\t\t$$$--------------------------------$$$");
                        System.out.println();
                        System.out.println("Your Transaction History");
                        System.out.println();
                        long acc=rs.getLong(4);
                        f=new File(acc+".txt");
                        FileReader fr=new FileReader(f);
                        BufferedReader br=new BufferedReader(fr);
                        String i;
                        while((i=br.readLine())!=null){
                            System.out.println(i);
                        }
                        System.out.println("\t\t$$$--------------------------------$$$");
                        br.close();
                    }
                    if(flag==0){
                        System.out.println("\u001B[31m"+"\n\t\t-------------------!!!\n\t\tInvalid Pin..!!\n\t\t-------------------!!!\n\n"+"\u001B[0m");
                    }
                    Thread.sleep(500);
                    break;
                }
                case "5":{
                    p.visiting();
                    sc.close();
                 //   fw.close();
                  //  bw.close();
                    System.exit(0);
                }
                default:{
                    System.out.println("\u001B[31m"+"\n\n-------------------!!!\nInvalid Choice..!!\n-------------------!!!\n\n"+"\u001B[0m");
                    Thread.sleep(500);
                break;
                }
            }
        }
    }
     catch (Exception e) {
        System.out.println(e.getMessage());
       }
    }
}
class Print{
    String black="\u001B[30m";
    String red="\u001B[31m";
    String green="\u001B[32m";
    String yellow="\u001B[33m";
    String blue="\u001B[34m";
    String purpule="\u001B[35m";
    String cyan="\u001B[36m";
    String white="\u001B[37m";
    String reset = "\u001B[0m";
    void Welcome(){
        System.out.println(green);
        System.out.println("                   ****************************************************");
        System.out.println("                   *              Welcome!                            *");
        System.out.println("                   *              Please Insert Your Card.            *");
        System.out.println("                   ****************************************************");
        System.out.println(reset);
    }
    // void donotremove(){
    //     System.out.println(green);
    //     System.out.println("                   ****************************************************");
    //     System.out.println("                   *         Please Do Not Remove Your Card.          *");
    //     System.out.println("                   *         Leave Your Card Inserted during          *");
    //     System.out.println("                   *         The Entire Transaction.                  *");
    //     System.out.println("                   ****************************************************");
    //     System.out.println(reset);
    // }
    void mainmenu(){
        System.out.println(blue);
        System.out.println("                   ******************************************************");        
        System.out.println("                   *                 Please Select Your Choice          *");
        System.out.println("                   ******************************************************");        
        System.out.println("                   * 1] Banking                     2] Balance inquiry  *");
        System.out.println("                   *                                                    *");
        System.out.println("                   * 3] Fund Transfer               4] Mini Statement   *");
        System.out.println("                   *                                                    *"); 
        System.out.println("                   * 5] Exit                                            *"); 
        System.out.println("                   ******************************************************");        
        System.out.println(reset);
      }
    void bankimgmenu(){
        System.out.println(blue);
        System.out.println("                  ******************************************************");        
        System.out.println("                  *                 Banking                            *");
        System.out.println("                  ******************************************************");        
        System.out.println("                  * 1] Pin change                 2] Withdraw Cash     *");
        System.out.println("                  *                                                    *");
        System.out.println("                  * 3] Deposit Cash               4] Generate ATM Pin  *");
        System.out.println("                  *                                                    *"); 
        System.out.println("                  * 5] Go Back                    6] Exit              *");
        System.out.println("                  ******************************************************");        
        System.out.println(reset);
      }
    void pinchange(){
        System.out.println();
        System.out.println(purpule);
        System.out.println("                  ******************************************************");        
        System.out.println("                  *                                                    *");
        System.out.println("                  *             Pin Change Successfully.               *");
        System.out.println("                  *                                                    *");
        System.out.println("                  ******************************************************");        
        System.out.println(reset);
      }
    void pinchangefail(){
            System.out.println();
            System.out.println(red);
            System.out.println("               ******************************************************");
            System.out.println("               *                                                    *");
            System.out.println("               *           !!!! Pin Change Failed !!!!              *");
            System.out.println("               *                                                    *");
            System.out.println("               ******************************************************");
            System.out.println(reset);
            System.out.println();
      }
    void accounttype(){
            System.out.println();
            System.out.println(cyan);
            System.out.println("               ******************************************************");
            System.out.println("               *               Select Account Type                  *");
            System.out.println("               ******************************************************");
            System.out.println("               *               1] Current Account                   *");
            System.out.println("               *                                                    *");
            System.out.println("               *               2] Saving Account                    *");
            System.out.println("               ******************************************************");
            System.out.println(reset);
      }
    void Transactioncomplete(ResultSet rs) throws Exception{
            System.out.println();
            System.out.println(green);
            System.out.println("               ******************************************************");
            System.out.println("               *       Transaction Successfully...                  *");
            System.out.println("               *       Remaining Balance: "+rs.getDouble(5)+"                  *");
            System.out.println("               ******************************************************");
            System.out.println(reset);
      }
    void otpsend(){
            System.out.println();
            System.out.println(purpule);
            System.out.println("               ******************************************************");
            System.out.println("               *        Otp is Sent To Your Registered Mobile       *");
            System.out.println("               *        Number.                                     *");
            System.out.println("               *                                                    *");
            System.out.println("               *        Enter OTP                                   *");
            System.out.println("               ******************************************************");
            System.out.println(reset);
      }
    void Collectrecipt(){
            System.out.println();
            System.out.println(purpule);
            System.out.println("             ******************************************************");
            System.out.println("             *            Please Collect Your  Receipt            *");
            System.out.println("             ******************************************************");
            System.out.println(reset);
      }
    void visiting(){
            System.out.println();
            System.out.println(purpule);
            System.out.println("              *****************************************************");
            System.out.println("              *             THANK YOU..                           *");
            System.out.println("              *             FOR VISITING OUR ATM                  *");
            System.out.println("              *****************************************************");
            System.out.println(reset);    
      }
    void invalidpin(){
             System.out.println();
             System.out.println(red);
            System.out.println("              ******************************************************");
            System.out.println("              *             Transaction Failed                     *");
            System.out.println("              *             !!! Invalid PIN !!!                    *");
            System.out.println("              ******************************************************");
            System.out.println(reset);
      }
    void invalidaccountnumber(){
            System.out.println();
            System.out.println(red);
            System.out.println("              ******************************************************");
            System.out.println("              *             Transaction Failed                     *");
            System.out.println("              *             !!! Invalid Account Number !!!         *");
            System.out.println("              ******************************************************");
            System.out.println(reset);
      }
    void Insufficientbalance(){
            System.out.println();
            System.out.println(red);
            System.out.println("              ******************************************************");
            System.out.println("              *             Transaction Failed                     *");
            System.out.println("              *             !!! Insufficient Balance !!!           *");
            System.out.println("              ******************************************************");
            System.out.println(reset);
      }
    void Balance(ResultSet rs) throws Exception{
            System.out.println();
            System.out.println(green);
            System.out.println("              *******************************************************");
            System.out.println("              *              Balance: "+rs.getInt(5)+"                        *");
            System.out.println("              *******************************************************");
            System.out.println(reset);
      }
    void invalidotp(){
            System.out.println();
            System.out.println(red);
            System.out.println("               *******************************************************");
            System.out.println("               *            Transaction Failed                       *");
            System.out.println("               *            !!! Invalid OTP !!!                      *");
            System.out.println("               *******************************************************");
            System.out.println(reset);
}
}