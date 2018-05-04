    import java.util.Arrays;
    import java.util.Scanner;
    import java.util.Comparator;

public class Main {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        int totalMatch, nTeam, nFP, nS, nPlayers, nPlayer;
        System.out.println("SLC for CVRP");

        //Input CVRP
        //Jumlah Kendaraan
        System.out.print("Jumlah kendaraan = ");
        int vehicleNum = 2;
        //int vehicleNum = input.nextInt();

        //Kapasitas
        System.out.print("Kapasitas Kendaraan= ");
        int capacity = 100;
        //int capacity = input.nextInt();

        //Jumlah Pelanggan
        System.out.print("Jumlah pelanggan = ");
        int customerNum = 5;
        //int customerNum = input.nextInt();
        int resetcustomerNum = customerNum;

        //Input SLC
        //Jumlah Tim
        System.out.print("Masukan Jumlah Tim = ");
        nTeam = 5;
        //nTeam = input.nextInt();

        //Jumlah Pertandingan
        totalMatch = (nTeam * (nTeam - 1)) / 2;
        System.out.println("Jumlah pertandingan ada = " + totalMatch);

        //Jumlah Pemain Inti
        System.out.print("Masukan Jumlah Fixed Player = ");
        nFP = 5;
        //nFP = input.nextInt();

        //Pemain Cadangan
        System.out.print("Masukan Jumlah Substitute Player = ");
        nS = 5;
        //nS = input.nextInt();

        //jumlah pemain setiap tim
        nPlayer = nFP + nS;
        System.out.println("Jumlah Pemain Satu Tim = " + nPlayer);

        //jumlah pemain total
        nPlayers = nTeam * (nFP + nS);
        System.out.println("Jumlah Pemain Satu Liga = " + nPlayers);


        //Demand
        int[] demand = new int[customerNum + 1];
        System.out.println("Demand Pelanggan = ");
        for (int i = 1; i <= customerNum; i++) {
            demand[i] = input.nextInt();
        }
        System.out.println("");

        for (int i = 1; i <= customerNum; i++) {
            System.out.println("Demand Pelanggan " + i + " = " + demand[i]);
        }

        //Matriks Jarak
        double[][] matriksjarak = new double[customerNum + 1][customerNum + 1];
        System.out.println("Matriks Jarak depot ke pelanggan = ");
        for (int i = 0; i <= (customerNum); i++) {
            for (int k = 0; k <= (customerNum); k++) {
                matriksjarak[i][k] = input.nextDouble();
            }
        }

        for (int i = 0; i <= (customerNum); i++) {
            for (int k = 0; k <= (customerNum); k++) {
                System.out.format("%7.2f", matriksjarak[i][k]);
            }
            System.out.println("");
        }
        System.out.println("");

        //Koordinat x dan y Pelanggan jangan masukin depot
        double[] coorx = new double[customerNum];
        double[] coory = new double[customerNum];
        System.out.println("Masukkan Koordinat X pelanggan (tanpa depot): ");
        for (int i = 0; i < customerNum; i++) {
            coorx[i] = input.nextDouble();
        }
        System.out.println("");
        System.out.println("Masukkan Koordinat y pelanggan (tanpa depot): ");
        for (int i = 0; i < customerNum; i++) {
            coory[i] = input.nextDouble();
        }
        System.out.println("");

        //Koordinat semua Pelanggan jangan masukin depot
        System.out.println("Masukin koordinat semua pelanggan (X dan Y tanpa depot)");
        int jumlahkoor;
        jumlahkoor = customerNum * 2;
        double[] coor = new double[jumlahkoor];
        for (int i = 0; i < jumlahkoor; i++) {
            coor[i] = input.nextDouble();
        }

        Arrays.sort(coor);
        double coorMin = coor[0];
        double coorMax = coor[coor.length-1];
        System.out.println("Coormin = " + coorMin);
        System.out.println("Coormax = " + coorMax);


        //Penentuan Atribut Awal Pemain
        double player[][] = new double[nPlayers][(3 * vehicleNum)];
        for (int i = 0; i < nPlayers; i++) {
            System.out.println("Pemain " + (i + 1) + " :");
            for (int j = 0; j < (3 * vehicleNum); j++) {
                player[i][j] = (Math.random() * (coorMax - coorMin)) + coorMin;
                System.out.format("%8.2f", player[i][j]);
            }
            System.out.println("");
        }

        //List Pelanggan
        int customerList[][]=new int[nPlayers][customerNum];
        for(int c=0;c<nPlayers;c++){
            int z=1;
            for(int a=0;a<customerNum;a++){
                customerList[c][a]=z;
                z++;
            }
        }

        //Urutin list pelanggan berdasarkan jarak dari depot terbesar
        double sementara5=-10000;
        int simpan =0;
        int tukar = 0;
        int posisi =0;

        for(int a=0;a<nPlayers;a++){
            for(int b=0;b<(customerNum-1);b++){
                sementara5 = -10000;
                for(int c=b;c<customerNum;c++){
                    if(matriksjarak[0][customerList[a][c]]>sementara5){
                        simpan = customerList[a][c];
                        posisi=c;
                        sementara5 = matriksjarak[0][customerList[a][c]];
                    }
                }
                tukar=customerList[a][b];
                customerList[a][b]=simpan;
                customerList[a][posisi]=tukar;

            }
        }

        //Buat Rute
        double[][]vehicle = new double[vehicleNum][3];
        int rute[][][] = new int[nPlayers][vehicleNum][customerNum];
        int [][]banya = new int[nPlayers][vehicleNum];
        bentukrute(capacity,simpan,posisi,sementara5,resetcustomerNum,customerList,vehicleNum, vehicle, player, coorx, coory, matriksjarak, customerNum, nPlayers, rute, banya, demand, tukar);


        //2opt local improvement di setiap rute
        int [][]dummyfit = new int[nPlayers][vehicleNum];
        int bestrute[][][] = new int[nPlayers][vehicleNum][customerNum];
        twoopt(simpan,banya,vehicleNum, bestrute,rute,dummyfit, nPlayers,matriksjarak,customerNum);


        //Hitung Fitness
        int []fitness = new int[nPlayers];
        double[][]minpemain = new double[nPlayers][(3*vehicleNum)];
        int[][] rutemin = new int[customerNum][customerNum];
        hitungfitness(vehicleNum,customerNum,rute,fitness,nPlayers,matriksjarak,player,minpemain,rutemin);

        int t=0;
        customerNum = resetcustomerNum;

        //masukan pemain ke array baru buat diurutin
        double newPlayerArray [][] = new double[nPlayers][(vehicleNum*3)+1];
        for (int i = 0; i < nPlayers; i++) {
            for (int j = 0; j < (vehicleNum*3); j++) {
                newPlayerArray[i][j] = player [i][j];
                newPlayerArray[i][(vehicleNum*3)] = fitness[i];

            }
        }

        /*for (int i = 0; i < nPlayers; i++) {
            System.out.println("Pemain " + (i+1) +" : ");
            for (int j = 0; j < (vehicleNum*3)+1; j++) {
                System.out.format("%8.2f", newPlayerArray[i][j]);

            }
            System.out.println("");
        }
        System.out.println("");
        System.out.println(""); */

        //sorting kecil ke besar
        Arrays.sort(newPlayerArray, new Comparator<double[]>() {
            @Override
            public int compare(double[] o1, double[] o2) {
                return Double.compare(o1[(vehicleNum*3)], o2[(vehicleNum*3)]);
            }//sort kecil ke besar
        });


        System.out.println("Atribut Pemain setelah diurutkan dari fitness paling kecil :");
        System.out.println("");

        //Print ulang array yang udah disort
        for (int i = 0; i < nPlayers; i++) {
            System.out.println("Pemain " + (i + 1) + " :");
            for (int j = 0; j < (3 * vehicleNum)+1; j++) {
                System.out.format("%8.2f", newPlayerArray[i][j]);
            }
            System.out.println("");
        }

        System.out.println("");
        System.out.println("");

        //Super Star Player
        double superstarplayer[] = new double[(vehicleNum*3)+1];
        //star player
        double starplayer[][] = new double[100][100];
        double min = 99999;

        hitungsuperstarplayer(nPlayers, min, newPlayerArray, vehicleNum, superstarplayer);
        hitungstarplayer(nPlayer, min, newPlayerArray, vehicleNum, starplayer, nTeam, nFP);

        //Masukin ke tim dan cari star player
        //double tim[][][] = new double[nTeam][nPlayers][(vehicleNum*3)+1];

        //masuk ke tim
        //masuktim(tim,vehicleNum,newPlayerArray,nPlayer,nTeam,nPlayers);

        double [] teampower = new double[nTeam];
        hitungteampower(teampower,vehicleNum,newPlayerArray,nPlayer,nTeam);


        double dummyPemain [][] = new double[nPlayers][customerNum*3+1];
        int ruteImitasi [][][] = new int[nPlayers][vehicleNum][customerNum];
        int bestruteImitasi[][][] = new int[nPlayers][vehicleNum][customerNum];

        for (int team = 0; team< nTeam;team++){
            for (int acuan = 0; acuan < nTeam; acuan++) {
                double miu1;
                double miu2;
                double tao1;
                double tao2;
                double x1;
                double x2;
                double TPk = teampower[team];
                double TPi = teampower[acuan];
                double PVk,PVi;
                if (team!=acuan){
                    PVk = TPk/(TPi+TPk);
                    PVi = 1-PVk;
                    double MR = Math.random();
                    if (PVk>MR){
                        //lakukan imitasi pada team;
                        for (int pemain = team*nPlayer; pemain < team*nPlayer+nFP; pemain++) {
                            for (int dim = 0; dim < vehicleNum*3; dim++) {
                                miu1 = (Math.random() * (1 - 0.5)) + 0.5;
                                //System.out.println(miu1);
                                //System.out.println("");
                                miu2 = (Math.random() * (0.5 - 0)) + 0;
                                //System.out.println(miu2);
                                //System.out.println("");
                                tao1 = (Math.random() * (1 - 0)) + 0;
                                //System.out.println(tao1);
                                //System.out.println("");
                                tao2 = (Math.random() * (1 - 0)) + 0;
                                //System.out.println(tao2);
                                //System.out.println("");
                                x1 = (Math.random() * (1 - 0.9)) + 0.9;
                                //System.out.println(x1);
                                //System.out.println("");
                                x2 = (Math.random() * (0.6 - 0.4)) + 0.4;
                                //System.out.println(x2);
                                //System.out.println("");
                                dummyPemain [pemain][dim] = (miu1*newPlayerArray[pemain][dim])+(tao1*(superstarplayer[dim]-newPlayerArray[pemain][dim]))+(tao2*(starplayer[team][dim]-newPlayerArray[pemain][dim]));
                                //System.out.format("%8.2f", dummyPemain[pemain][dim]);
                                bentukrute(capacity,simpan,posisi,sementara5,resetcustomerNum,customerList,vehicleNum, vehicle, dummyPemain, coorx, coory, matriksjarak, customerNum, nPlayers, ruteImitasi, banya, demand, tukar);
                                /*twoopt(simpan,banya,vehicleNum,bestruteImitasi,rute,dummyfit,nPlayers,matriksjarak,customerNum);
                                hitungfitness(vehicleNum,customerNum,rute,fitness,nPlayers,matriksjarak,player,minpemain,rutemin);
                                hitungsuperstarplayer(nPlayers, min, newPlayerArray, vehicleNum, superstarplayer);
                                hitungstarplayer(nPlayer, min, newPlayerArray, vehicleNum, starplayer, nTeam, nFP);
                                for (int countplayer = 0; countplayer < nPlayers; countplayer++) {
                                        newPlayerArray[countplayer][(vehicleNum*3)] = fitness[countplayer];

                                }*/


                            }

                            System.out.println("");
                        }
                    } else{
                        //lakukan inmitasi pada acuan;
                        break;
                    }
                }
            }
        }


    }

    public static void hitungsuperstarplayer(int nPlayers, double min, double [][] newPlayerArray, int vehicleNum, double []superstarplayer){
        System.out.println("Super Star Player =");
        for (int i = 0; i < nPlayers; i++) {
            if (min>newPlayerArray[i][vehicleNum*3]){
                min = newPlayerArray[i][vehicleNum*3];
                for (int j = 0; j < (vehicleNum*3)+1; j++) {
                    superstarplayer[j] = newPlayerArray[i][j];
                }
            }
        }
        for (int j = 0; j < (vehicleNum*3)+1; j++) {
            System.out.format("%8.2f", superstarplayer[j]);
        }

        System.out.println("");
        System.out.println("");
    }

    public static void hitungstarplayer(int nPlayer, double min, double [][] newPlayerArray, int vehicleNum, double [][]starplayer, int nTeam, int nFP){
        System.out.println("Star Player =");
        for (int i = 0; i < nTeam; i++) {
            min = 99999;
            for (int team = nPlayer*i; team < (i*nPlayer)+nFP; team++) {
                if (min>newPlayerArray[team][vehicleNum*3]){
                    min = newPlayerArray[team][vehicleNum*3];
                    for (int j = 0; j < (vehicleNum*3)+1; j++) {
                        starplayer[i][j] = newPlayerArray[team][j];
                    }
                }
            }
        }

        for (int i = 0; i < nTeam; i++) {
            for (int j = 0; j < (vehicleNum*3)+1; j++) {
                System.out.format("%8.2f", starplayer[i][j]);

            }
            System.out.println("");

        }
        System.out.println("");
        System.out.println("");
    }


    public static void hitungteampower(double [] teampower, int vehicleNum, double [][] newPlayerArray, int nPlayer, int nTeam){
        for (int i = 0; i < nTeam; i++) {
            double hitung = 0;
            for (int j = 0; j < nPlayer; j++) {
                hitung += newPlayerArray[(i*nPlayer)+j][vehicleNum * 3];
            }
            teampower[i] = hitung/nPlayer;
            //System.out.println(teampower[i]);
        }
    }

    /*public static void masuktim(double [][][] tim, int vehicleNum, double [][] newPlayerArray, int nPlayer, int nTeam, int nPlayers){
        int counterTim = 0;
        int counterTim2 = 0;
        int counterTim3;

        do {
            counterTim3 = 0;
            System.out.println("Tim "+ (counterTim+1) + ":");
            do {
                for (int i = 0; i < (vehicleNum*3)+1; i++) {
                    tim[counterTim][counterTim2][i] = newPlayerArray[counterTim2][i];
                    System.out.format("%8.2f", tim[counterTim][counterTim2][i]);

                }
                System.out.println("");
                counterTim2++;
                counterTim3++;
            }while(counterTim3<nPlayer);

            counterTim++;
        }while (counterTim<nTeam&&counterTim2<nPlayers);

    }*/

    public static void hitungfitness(int vehicleNum, int customerNum, int [][][] rute, int [] fitness, int nPlayers,double[][] matriksjarak,double player[][],double[][]minpemain,int[][] rutemin){
        int count=0;
        int start = 0;
        for (int i = 0; i < nPlayers; i++) {
            count=0;
            //System.out.println("Pemain " + (i + 1));
            for (int a = 0; a < vehicleNum; a++) {//rute
                start = 0;
                //if (rute[i][a][0] != 0) {
                //System.out.print("Rute " + (a + 1) + " : Depot ");
                //}
                for (int b = 0; b < customerNum; b++) {
                    if (rute[i][a][b] != 0) {
                        //System.out.print(rute[i][a][b] + " ");
                        fitness[i] += matriksjarak[start][rute[i][a][b]];
                        start = rute[i][a][b];
                        count+=1;
                    }
                }
                fitness[i] += matriksjarak[start][0];
                //if (rute[i][a][0] != 0) {
                //System.out.println("Depot");
                //}
            }
            if(count<customerNum){
                fitness[i]=100000;
            }
            System.out.println("Total Fitness Pemain " +(i+1) +" = "+ fitness[i] + "\n");
        }

        //Save solusi terbaik
        int fitnessmin = 10000;
        int pemainmin = 0;
        int tmin = 0;
        for (int i = 0; i < nPlayers; i++) {
            if (fitness[i] < fitnessmin) {
                fitnessmin = fitness[i];
                pemainmin = i;
                rutemin = rute[i];
                tmin = 0;
                for (int j = 0; j <(3*vehicleNum); j++) {
                    minpemain[pemainmin][j] = player[i][j];
                }
            }
        }

        //Print Solusi Terbaik
        System.out.println("Solusi Setelah Local Improvement : ");
        System.out.println("Fitness terkecil : " + fitnessmin);
        System.out.println("Pemain ke " + (pemainmin + 1));
        System.out.print("Atribut Pemain : ");
        for (int i = 0; i <(3*vehicleNum); i++) {
            System.out.format("%8.2f",minpemain[pemainmin][i]);
        }

        System.out.println("\nRute saat itu : ");
        for (int j = 0; j < vehicleNum; j++) {
            if (rutemin[j][0] != 0) {
                System.out.print("Rute " + (j + 1) + " : Depot ");
            }
            for (int k = 0; k < customerNum; k++) {
                if (rutemin[j][k] != 0) {
                    System.out.print(rutemin[j][k] + " ");
                }
            }
            if (rutemin[j][0] != 0) {
                System.out.println("Depot");
            }
        }

    }


    public static void bentukrute(int capacity, int simpan, int posisi, double sementara5, int resetcustomerNum, int [][] customerList, int vehicleNum, double [][] vehicle, double [][] player,double [] coorx, double [] coory,double[][] matriksjarak, int customerNum, int nPlayers, int[][][] rute, int [][] banya, int []demand, int tukar){
        int sementara2 = 0;
        int cek1 =0;
        int[] kapa = new int[vehicleNum];

        //Tentukan referensi kendaraan (koordinat dan radius pelayanan)
        for(int i=0;i<nPlayers;i++){//Setiap Pemain
            customerNum=resetcustomerNum;
            //for (int j = 0; j < customerNum; j++) {
            //  System.out.println(customerList[i][j]);
            //System.out.println("");
            //}
            int b=0;
            for(int j=0;j<vehicleNum;j++){//Setiap Kendaraan
                for(int k=0;k<3;k++){//Setiap Dimensi Kendaraan
                    vehicle[j][k]=player[i][b];
                    b+=1;
                }
            }

            //Hitung Jarak dari referensi kendaraan dengan semua pelanggan
            double [][]distance = new double[vehicleNum][customerNum];
            double sementara3 = 0;
            for(int a=0;a<vehicleNum;a++){
                for(int c=0;c<customerNum;c++){
                    sementara3+=Math.pow((vehicle[a][0]-coorx[c]),2)+Math.pow((vehicle[a][1]-coory[c]),2);
                    distance[a][c] = Math.sqrt(sementara3);
                    sementara3=0;
                    //System.out.print("jarak kendaraan "+(a+1)+" dengan pelanggan "+(c+1)+" = " + distance[a][c]);
                    //System.out.println("");
                }
                System.out.println("");
            }

            //Masukin Ke mungkin semua pelanggan yang mungkin dimasukin
            int [][]mungkin = new int [vehicleNum][customerNum];
            int [][]hitung = new int [nPlayers][vehicleNum];
            for(int a=0;a<vehicleNum;a++){
                int c=0;
                System.out.print("Kendaraan "+(a+1)+" = ");

                hitung[i][a]=0;
                for(b=0;b<customerNum;b++){
                    if(distance[a][b]<=vehicle[a][2]){
                        mungkin[a][c] = b+1;
                        System.out.format("%5d",mungkin[a][c]);

                        c++;
                        hitung[i][a]++;
                    }
                }
                System.out.println("");
            }

            //Cek apakah ada pelanggan yang sama di mungkin
            for(int a=0;a<(vehicleNum-1);a++){
                for(b=(a+1);b<vehicleNum;b++){
                    if((hitung[i][a]>0)&&(hitung[i][b]>0)){
                        for(int c=0;c<hitung[i][a];c++){//pelanggan yang mungkin dilayani kendaraan 1
                            for(int d=0;d<hitung[i][b];d++){//pelanggan yang mungkin dilayani kendaraan 2
                                if(mungkin[a][c]==mungkin[b][d]){
                                    if(distance[a][mungkin[a][c]-1]<distance[b][mungkin[b][d]-1]){
                                        //Hapus yang di pelanggan di kendaraan 2
                                        for(int e=d;e<(hitung[i][b]-1);e++){
                                            mungkin[b][e]=mungkin[b][e+1];
                                        }
                                        hitung[i][b]--;
                                    }else{
                                        //Hapus pelanggan yang di kendaraan 1
                                        for(int e=c;e<(hitung[i][a]-1);e++){
                                            mungkin[a][e]=mungkin[a][e+1];
                                        }
                                        hitung[i][a]--;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            //Urutin Pelanggan di mungkin yang uda dihapus berdasarkan jarak
            sementara5=10000;
            simpan =0;
            posisi =0;
            for(int a=0;a<vehicleNum;a++){
                if(hitung[i][a]>0){
                    for(b=0;b<(hitung[i][a]-1);b++){//pelanggan di rute a
                        sementara5 = 10000;
                        for(int c=b;c<hitung[i][a];c++){
                            if(distance[a][mungkin[a][c]-1]<sementara5){
                                simpan = mungkin[a][c];
                                posisi=c;
                                sementara5 = distance[a][mungkin[a][c]-1];
                            }
                        }
                        tukar=mungkin[a][b];
                        mungkin[a][b]=simpan;
                        mungkin[a][posisi]=tukar;
                    }
                }
            }

            //Masukin Pelanggan mungkin ke rute
            int rutepos[];
            kapa = new int[vehicleNum];
            Arrays.fill(kapa,capacity);
            rutepos=new int[vehicleNum];
            for(int a=0;a<vehicleNum;a++){
                if(hitung[i][a]>0){
                    for(b=0;b<hitung[i][a];b++){
                        if ((kapa[a] - demand[mungkin[a][b]]) >= 0) {
                            rute[i][a][rutepos[a]] = mungkin[a][b];
                            kapa[a] = kapa[a] - demand[mungkin[a][b]];
                            banya[i][a]++;
                            rutepos[a]++;
                        }
                    }
                }
            }

            //Hapus daftar pelanggan yang uda masuk ke rute dari daftar pelanggan
            for(int a=0;a<vehicleNum;a++){
                for(b=0;b<rutepos[a];b++){
                    for(int z=0;z<customerNum;z++){
                        if(customerList[i][z]==rute[i][a][b]){
                            //Hapus pelanggan di daftar pelanggan
                            for(int e=z;e<(customerNum-1);e++){
                                customerList[i][e]=customerList[i][e+1];
                            }
                            customerNum--;
                        }
                    }
                }
            }

            //Bentuk rute dari sisa pelanggan yang belum dilayani
            double sementara6;
            double sementara7;
            if(customerNum>=0){
                int prioritas=0;
                for(int a=0;a<customerNum;a++){//pelanggan yang belum dilayani
                    sementara6=10000;
                    sementara7=10000;
                    for(b=0;b<vehicleNum;b++){//kendaraan
                        //cari prioritas kendaraan
                        if(distance[b][customerList[i][a]-1]<sementara6){
                            sementara6=distance[b][customerList[i][a]-1];
                            prioritas=b;
                        }
                    }
                    if(kapa[prioritas]-demand[customerList[i][a]]>=0){
                        kapa[prioritas] = kapa[prioritas] - demand[customerList[i][a]];
                        rute[i][prioritas][rutepos[prioritas]]=customerList[i][a];
                        banya[i][prioritas]++;
                        rutepos[prioritas]++;
                    }else{
                        break;
                    }
                }

                //Hapus daftar pelanggan yang uda masuk ke rute dari daftar pelanggan
                for(int a=0;a<vehicleNum;a++){
                    for(b=0;b<rutepos[a];b++){
                        for(int z=0;z<customerNum;z++){
                            if(customerList[i][z]==rute[i][a][b]){
                                //Hapus pelanggan di daftar pelanggan
                                for(int e=z;e<(customerNum-1);e++){
                                    customerList[i][e]=customerList[i][e+1];
                                }
                                customerNum--;
                            }
                        }
                    }
                }
            }
            else{
                break;
            }


            if(customerNum>=0){
                for (b = 0; b < customerNum; b++) {//tiap pelanggan yang belum dilayani
                    for (int a = 0; a < vehicleNum; a++) {//tiap rute
                        if ((kapa[a] - demand[customerList[i][b]]) >= 0) {
                            rute[i][a][rutepos[a]] = customerList[i][b];
                            kapa[a] = kapa[a] - demand[customerList[i][b]];
                            banya[i][a]++;
                            rutepos[a]++;
                            break;
                        }
                    }
                }
            }else{
                break;
            }

        }
        customerNum=resetcustomerNum;


    }

    public static void twoopt(int simpan, int [][] banya, int vehicleNum, int [][][] bestrute, int [][][] rute,int[][] dummyfit, int nPlayers,double[][] matriksjarak, int customerNum){
        int mulai=0;
        int dummy = 0;
        int cek=0;
        int simpanb =0;
        int simpanc =0;
        int simpanpel =0;

        for(int i=0;i<nPlayers;i++){
            for(int a=0;a<vehicleNum;a++){
                dummy=100000000;
                do{
                    cek=0;
                    simpanb=0;
                    simpanc=0;
                    simpan=0;
                    simpanpel=0;
                    for (int b = 0; b < banya[i][a]; b++) {
                        bestrute[i][a][b] = rute[i][a][b];
                    }

                    for(int b=0;b<banya[i][a];b++){
                        for(int c=0;c<banya[i][a];c++){
                            simpan = rute[i][a][b];
                            if(c<b){
                                for(int d=b;d>c;d--){
                                    rute[i][a][d]=rute[i][a][d-1];
                                }
                                rute[i][a][c]=simpan;
                            }else if(c>b){
                                for(int d=b;d<c;d++){
                                    rute[i][a][d]=rute[i][a][d+1];
                                }
                                rute[i][a][c]=simpan;
                            }
                            dummyfit[i][a]=0;
                            mulai=0;
                            for (int j = 0; j < customerNum; j++) {//hitung fitness
                                if (rute[i][a][j] != 0) {
                                    dummyfit[i][a] += matriksjarak[mulai][rute[i][a][j]];
                                    mulai = rute[i][a][j];
                                }
                            }
                            dummyfit[i][a] += matriksjarak[mulai][0];
                            if(dummyfit[i][a]<dummy){
                                dummy=dummyfit[i][a];
                                simpanb = b;
                                simpanc = c;
                                simpanpel=simpan;
                                cek=1;
                            }
                            for (int e = 0; e < banya[i][a]; e++) {//reset ke rute awal
                                rute[i][a][e] = bestrute[i][a][e];
                            }
                        }
                    }
                    if(simpanc<simpanb){
                        for(int d=simpanb;d>simpanc;d--){
                            rute[i][a][d]=rute[i][a][d-1];
                        }
                        rute[i][a][simpanc]=simpanpel;
                    }else if(simpanc>simpanb){
                        for(int d=simpanb;d<simpanc;d++){
                            rute[i][a][d]=rute[i][a][d+1];
                        }
                        rute[i][a][simpanc]=simpanpel;
                    }
                }while(cek==1);
            }
        } // tutup loop pemain

    }}

