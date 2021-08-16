package com.danebrown.algtech;

import com.danebrown.algtech.algcomp.AlgCompImpl;
import com.danebrown.algtech.algcomp.AlgCompMenu;
import com.danebrown.algtech.algcomp.AlgName;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Created by danebrown on 2021/8/2
 * mail: tain198127@163.com
 *
 * @author danebrown
 */
@Log4j2
public class MergeSort {
    public static void main(String[] args) {
        AlgCompMenu.addComp(new RecursionMergeSort());
        AlgCompMenu.addComp(new WhileMergeSort());
        AlgCompMenu.addComp(new RecLeftMinSum());
        AlgCompMenu.addComp(new LeftMinSum());
        AlgCompMenu.addComp(new ReversSortPair());
        AlgCompMenu.addComp(new BiggerThanRightTwice());
        AlgCompMenu.addComp(new CountOfRangeSum());
        AlgCompMenu.run();
    }

    @AlgName("递归归并排序[完成]")
    public static class RecursionMergeSort extends AlgCompImpl<int[], int[]> {

        @Override
        public int[] prepare() {
            int dataSize = ThreadLocalRandom.current().nextInt(2, 500000);
            int[] data = new int[dataSize];
            for (int i = 0; i < dataSize; i++) {
                data[i] = ThreadLocalRandom.current().nextInt();
            }
            return data;
        }

        @Override
        protected int[] standard(int[] data) {
            Arrays.sort(data);
            return data;
        }

        @Override
        protected int[] test(int[] data) {
            mergeSort(data);
            return data;
        }

        private void mergeSort(int[] data) {
            mergeSort(data, 0, data.length - 1);//这里容易出错
        }

        private void mergeSort(int[] data, int L, int R) {
            if (data == null || data.length < 2 || L == R) {
                return;
            }
            /**
             * 这里很容易出错
             */
            int middle = L + ((R - L) >> 1);//L + (R-L)/2 = R/2 + L/2=(R+L)/2
            mergeSort(data, L, middle);
            mergeSort(data, middle + 1, R);//这里容易出错
            merge(data, L, middle, R);
        }

        private void merge(int[] data, int L, int M, int R) {
            int i = 0;
            int[] tmp = new int[R - L + 1];
            int l = L;
            int r = M + 1;
            while (l <= M && r <= R) {//这里容易出错
                tmp[i++] = data[l] <= data[r] ? data[l++] : data[r++];//谁大用谁
            }
            while (l <= M) {
                tmp[i++] = data[l++];//
            }
            while (r <= R) {
                tmp[i++] = data[r++];
            }
            for (i = 0; i < tmp.length; i++) {
                data[L + i] = tmp[i];
            }
        }
    }

    /**
     * 循环版归并排序 第五章
     */
    @AlgName("循环归并排序")
    public static class WhileMergeSort extends AlgCompImpl<int[], int[]> {

        RecursionMergeSort recursionMergeSort = new RecursionMergeSort();

        @Override
        public int[] prepare() {
            return recursionMergeSort.prepare();
        }

        @Override
        protected int[] standard(int[] data) {
            return recursionMergeSort.standard(data);
        }

        @Override
        protected int[] test(int[] data) {
            whileSort(data);
            return data;
        }

        private void whileSort(int[] data) {
            if (data == null || data.length < 2) {
                return;
            }
            int N = data.length;
            int mergeSize = 1;
            while (mergeSize < N) {
                int L = 0;//当前左组的第一个位置
                while (L < N) {//这里很容易忘记
                    int M = L + mergeSize - 1;  //这里容易错
                    if (M >= N) {   //这里容易漏掉
                        break;
                    }
                    int R = Math.min(M + mergeSize, N - 1);   //这里容易错
                    merge(data, L, M, R);
                    L = R + 1; //这里容易漏掉
                }
                if (mergeSize > N / 2) {   //这里容易漏掉
                    break;
                }
                mergeSize <<= 1;

            }
        }

        private void merge(int[] data, int l, int m, int r) {
            int p1 = l;
            int p2 = m + 1;
            int[] tmp = new int[r - l + 1];
            int i = 0;
            while (p1 <= m && p2 <= r) {
                tmp[i++] = data[p1] <= data[p2] ? data[p1++] : data[p2++];
            }
            while (p1 <= m) {
                tmp[i++] = data[p1++];
            }
            while (p2 <= r) {
                tmp[i++] = data[p2++];
            }
            for (i = 0; i < tmp.length; i++) {
                data[i + l] = tmp[i];
            }
        }
    }

    /**
     * 第五章
     */
    /**
     * 在一个数组中，一个数左边比它小的数的总和，叫数的小和，所有数的小和累加起来，叫数组小和。求数组小和。
     * 例子： [1,3,4,2,5]
     * 1左边比1小的数：没有
     * 3左边比3小的数：1
     * 4左边比4小的数：1、3
     * 2左边比2小的数：1
     * 5左边比5小的数：1、3、4、 2
     * 所以数组的小和为1+1+3+1+1+3+4+2=16
     */
    @AlgName("递归版本最小和")
    public static class RecLeftMinSum extends AlgCompImpl<Integer, int[]> {
        @Override
        public int[] prepare() {
            int dataSize = ThreadLocalRandom.current().nextInt(2, 50000);
            int[] data = new int[dataSize];
            for (int i = 0; i < dataSize; i++) {
                data[i] = ThreadLocalRandom.current().nextInt();
            }
            return data;
        }

        @Override
        protected Integer standard(int[] data) {
            int ret = 0;
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < i; j++) {
                    if (data[j] < data[i]) {
                        ret += data[j];
                    }
                }
            }

            return ret;
        }

        @Override
        protected Integer test(int[] data) {
            int result = mergeSort(data, 0, data.length - 1);
            //            log.info("左侧最小和结果:{}", result);
            //            log.info("{}",data);
            return result;
        }

        private int mergeSort(int[] data, int l, int r) {
            int result = 0;

            if (l >= r) {
                return result;
            }
            int m = l + ((r - l) >> 2);
            result += mergeSort(data, l, m);
            result += mergeSort(data, m + 1, r);
            result += merge(data, l, m, r);
            return result;

        }

        private int merge(int[] data, int l, int m, int r) {
            int[] tmp = new int[r - l + 1];
            int p1 = l;
            int p2 = m + 1;
            int i = 0;
            int result = 0;
            while (p1 <= m && p2 <= r) {
                //左组小的时候产生小和.这句是精髓.把
                /**
                 * 把计算小和数的问题，转化成了，右边有几个比这个小数大的。有几个比他大的就乘以几次。
                 * 之所以能乘以几次，是因为每一次左组的时候，都已经是有序的了。也就是说，一旦发生了
                 * 左组数小，那么一定可以判定，右边的都比这个数大。
                 * 每一次递归，只算本次递归内的。下次递归计算下次的，这样算法累加起来，就把数组上所有
                 * 比这个数大的记录下来了
                 * 本质上是因为无论左侧还是右侧的数组，已经是有序的了。利用了上次计算的结果
                 */
                if (data[p1] < data[p2]) {
                    result += data[p1] * (r - p2 + 1);
                }
                tmp[i++] = data[p1] < data[p2] ? data[p1++] : data[p2++];
            }
            while (p1 <= m) {
                //                result += data[p1];
                tmp[i++] = data[p1++];
            }
            while (p2 <= r) {
                tmp[i++] = data[p2++];
            }
            for (i = 0; i < tmp.length; i++) {
                data[i + l] = tmp[i];
            }
            return result;
        }
    }

    @AlgName("非递归版本最小和")
    public static class LeftMinSum extends AlgCompImpl<Integer, int[]> {
        private RecLeftMinSum recLeftMinSum = new RecLeftMinSum();

        @Override
        public int[] prepare() {
            //            return new int[]{2,3,5,1,2,8,0,7,4};
            return recLeftMinSum.prepare();
        }

        @Override
        protected Integer standard(int[] data) {
            return recLeftMinSum.test(data);
        }

        @Override
        protected Integer test(int[] data) {
            int result = 0;

            if (data == null || data.length < 2) {
                return result;
            }
            int N = data.length;
            int step = 1;
            while (step < N) {
                int L = 0;
                while (L <= N) {
                    int M = L + step - 1;
                    if (M >= N) {
                        break;
                    }
                    int R = Math.min(M + step, N - 1);

                    result += merge(data, L, M, R);
                    L = R + 1;
                }
                if (step > N / 2) {
                    break;
                }
                step <<= 1;
            }


            //            log.info("排序结果:{}",data);
            return result;
        }

        protected int merge(int[] data, int l, int m, int r) {
            int p1 = l;
            int p2 = m + 1;
            int i = 0;
            int[] tmp = new int[r - l + 1];
            int result = 0;
            while (p1 <= m && p2 <= r) {
                result += data[p1] < data[p2] ? (data[p1] * (r - p2 + 1)) : 0;
                tmp[i++] = data[p1] < data[p2] ? data[p1++] : data[p2++];
            }
            while (p1 <= m) {
                tmp[i++] = data[p1++];
            }
            while (p2 <= r) {
                tmp[i++] = data[p2++];
            }
            for (i = 0; i < tmp.length; i++) {
                data[i + l] = tmp[i];
            }
            return result;
        }
    }

    /**
     * 第五章
     * 如果一个数组中，左边的数和右边的数组成逆序，我们称之为逆序对
     * 例如[3,1,0,4,3,1]
     * 其中
     * [3,1][3,0][3,1]
     * [1,0]
     * [4,3][4,1]
     * 都是逆序对
     * 这道题的本质就是在问
     * 右边有多少个数比他小
     * 思路：merge中从右往左对比拷贝，相等继续，当出现左边的数比右边的大时，右边有几个数，就表示有几个
     * 逆序对。因为在merge过程中，两边的数组本身已经有序了。利用的就是有序的特性
     * <p>
     * 问有几个逆序对
     */
    @AlgName("逆序对计算，经典考题")
    public static class ReversSortPair extends AlgCompImpl<Integer, int[]> {
        RecursionMergeSort recursionMergeSort = new RecursionMergeSort();

        @Override
        public int[] prepare() {
            int dataSize = ThreadLocalRandom.current().nextInt(2, 5000);
            int[] data = new int[dataSize];
            for (int i = 0; i < dataSize; i++) {
                data[i] = ThreadLocalRandom.current().nextInt();
            }
            return data;
        }

        @Override
        protected Integer standard(int[] data) {
            int count = 0;
            for (int i = 0; i < data.length; i++) {
                for (int j = i + 1; j < data.length; j++) {
                    if (data[i] > data[j]) {
                        count++;
                    }
                }
            }
            return count;
        }

        @Override
        protected Integer test(int[] data) {
            int result = mergeSort(data, 0, data.length - 1);
            return result;
        }

        public int mergeSort(int[] data, int l, int r) {
            int result = 0;
            if (l >= r || data == null || data.length < 2) {
                return result;
            }
            int m = l + ((r - l) >> 2);
            result += mergeSort(data, l, m);
            result += mergeSort(data, m + 1, r);
            result += merge(data, l, m, r);
            return result;
        }

        /**
         * 逆序对归并算法
         *
         * @param data 原始数组
         * @param l    左下标
         * @param m    中值下标
         * @param r    又下标
         * @return 逆序对个数
         */
        public int merge(int[] data, int l, int m, int r) {
            int i = r - l;
            int p1 = m;
            int p2 = r;
            int[] tmp = new int[r - l + 1];
            int result = 0;
            while (l <= p1 && (m + 1) <= p2) {
                if (data[p1] > data[p2]) {
                    result += (p2 - m);
                }
                tmp[i--] = data[p2] < data[p1] ? data[p1--] : data[p2--];
            }
            while (l <= p1) {
                tmp[i--] = data[p1--];
            }
            while (m + 1 <= p2) {
                tmp[i--] = data[p2--];
            }
            for (i = 0; i < tmp.length; i++) {
                data[i + l] = tmp[i];
            }
            return result;
        }
    }

    /**
     * 第五章
     * 右边有几个数乘以2以后，依旧小于当前的数
     */
    @AlgName("大于右侧数2倍")
    public static class BiggerThanRightTwice extends AlgCompImpl<Integer, int[]> {
        private ReversSortPair reversSortPair = new ReversSortPair();
        private WhileMergeSort whileMergeSort = new WhileMergeSort();

        @Override
        public int[] prepare() {
            int[] pre = {1396581779, -937854641, -1001942975, 331070449, 911494688, 1252112712, -518685889, -2099230105, 1499333421, 880179086, -455919723, -514633115, -1998371172, 1386217678, -1658555230, 1779130473, 353096652, -1824614786, 1965216959, -199689732, -291094162, -2100728129, 992008266, 460670078, 716830395, -246401178, 1294603821, 414326368, -715172082, -1816814831, 2102853410, 1762497825, -2009375183, -777500377, 1535365379, 1782993543, 574765802, -259883399, 483476288, 5703938, -888524454, -1620064806, 1115847622, 1668424119, -1846221551, -666834199, 697063222, 1752394276, -1331118453, -1175289589, -1411246138, 273538865, 502870178, 1312373603, -1071811756, -1789410356, 1857052552, 1428540809, 490850739, 2021876286, 864861532, 1771377927, 1574072311, -96906451, 1410721574, -370575140, 407567569, -201496245, 568066012, -707497121, -1739473629, 886489744, 1583009397, -267828497, -1483481871, 1445374274, 1725133062, -536398563, 763981830, 732398053, -1712532843, 1296181653, 1748211987, 296813020, -142938284, -1329572769, 1083242123, -1785397650, -424991517, 358242197, -575858682, 90222382, 1659847088, -1869800483, 984400793, 74527482, -817383843, -1405319904, 1870279591, 2000748639, 352190146, -1899603527, 614153144, 95256613, -181122506, -436979003, 191729519, 640668972, -509974264, 953811968, 849651543, -2040000665, -1427423600, -798345960, -930101255, -2056789176, 740773450, 1258353959, 1397355849, 846185042, -563080353, -1706803335, 805791325, 1481825647, 967029655, 1569719724, 321276456, 1734168489, -2118750503, -470866386, 1388970773, -723546843, -205945951, -268570370, -1536775768, -340045313, -1229745811, -180257518, -341431247, -679156374, 1715074136, -1701026229, -1881797016, -1266165047, 1199064320, 1053435882, 2138233458, -2127324531, -639798923, -1783458816, 909188144, -1433304285, 953691421, -211562504, 35782819, -2012873789, 303738750, 1812703280, 1958585033, 905821279, 70030717, -1263226494, 488273127, -835925154, -1594840, 1971876238, -833939252, 1255550943, -2091872372, -182871991, 868699519, 1755703838, 569718143, -839960885, -1186860832, 1101305776, -418079739, 280245321, -676720045, -1319422984, 1818342092, 685198082, 1134058718, -1822682205, 1922197683, 1236885330, 804529405, 1537176289, -2027599556, -458492907, 1608381090, 163831003, -1615447726, -1274820794, 1707953402, -1336060908, 184493398, -626310061, -718922494, 900688229, 145076874, 1274085285, 2143390770, -1120961282, 1856852480, 1309456334, -935181838, -1937433962, -613585973, 70499843, -755785833, 1061712325, 1074753807, -1940225292, -2145869501, -16330535, -1786132242, -305145198, -1992650829, 1238230276, -1142688777, 1369626562, 2089310706, -75349927, -972154738, -1424897211, 1482457435, 1079238806, -324911591, 1082901525, 955930200, 293236256, 809152097, -2131596103, 1093544174, -1112039219, 1374452041, 1351684051, -573562797, -881716273, -1238407091, -1122410217, 870780410, -640935540, -889070049, -1166007496, -1724286533, -695828765, -1060783425, -128536849, 1669239678, 221060881, -1341921571, 1704399214, 78827965, 443395127, 1179376198, 2144800052, 487574884, -1771758568, -432095623, -397183917, 922149232, -1211879336, 53790367, 1120410368, -264900898, -282404383, -230469922, 1870257548, 508321073, 1289784367, -1918570138, 1117157497, -1376304473, -771552264, 833438273, -1379376138, 1699260031, 1271440008, -966037556, -1894942009, -1708287755, -1003084802, 1958708605, -173772578, -1020537720, -473398897, 302228833, 762683916, -1120805874, 694167953, 191065512, 268632470, 1551414776, -358271264, -1497852413, -1426303574, -1796771555, 378223399, -1617926489, 1378735765, -848668565, -219400523, -1084170512, -523921955, -1370133733, 1150370503, -2082651537, -1841241698, -1225080215, -503054446, 517256559, 1417040815, 82044029, 1133669002, -1801406606, 1169740119, -1602055148, 1655115421, 58315070, -967431088, -1085103055, 1169515807, -1809041965, 854628907, 1703980343, 806233828, -660617462, -239741610, 513472302, -1989553365, -2101126771, 910251799, 139985363, 917287403, 1982742964, -821924898, -8041424, 131772648, 509226060, 425047353, -1288296800, 31037981, 41632449, -1148940223, 971454204, -1274176825, 1317427490, -1702923457, -979940201};
            //            int[] pre = {440025818, -1034771247, 627919729, 1315612600, -318406083, -208017622, 423997791, 1560085865};
            //            int[] pre = {440025818, -1034771247, 627919729, 1315612600};
            //            int[] pre = {8,3,1,18,2};
            //return pre;
            int dataSize = ThreadLocalRandom.current().nextInt(2, 50000);
            int[] data = new int[dataSize];
            for (int i = 0; i < dataSize; i++) {
                data[i] = ThreadLocalRandom.current().nextInt();
            }
            return data;
            //            return Arrays.stream(data).asLongStream().toArray();

        }

        @Override
        protected Integer standard(int[] data) {
            int count = 0;
            for (int i = 0; i < data.length; i++) {
                for (int j = i + 1; j < data.length; j++) {
                    if ((data[i] >> 1) > data[j]) {
                        count++;
                    }
                }
            }
            //            whileMergeSort.test(data);
            //            log.info("标准归并排序结果:{}",data);
            return count;
        }

        @Override
        protected Integer test(int[] data) {

            int count = 0;

            count = whileSort(data);
            //            count = mergeSort(data,0,data.length-1);
            //            log.info("测试归并排序结果:{}",data);

            return count;
        }

        public int whileSort(int[] data) {
            int count = 0;
            if (data == null || data.length < 2) {
                return count;
            }
            int N = data.length;
            int step = 1;
            while (step < N) {
                int L = 0;
                while (L < N) {
                    int M = L + step - 1;
                    if (M >= N) {
                        break;
                    }
                    int R = Math.min(M + step, N - 1);
                    count += merge(data, L, M, R);
                    L = R + 1;
                }
                if (step > (N >> 1)) {
                    break;
                }
                step <<= 1;
            }
            return count;
        }

        public int mergeSort(int[] data, int l, int r) {
            int count = 0;
            if (data == null || data.length < 2 || l == r) {
                return count;
            }
            int m = l + ((r - l) >> 1);
            count += mergeSort(data, l, m);
            count += mergeSort(data, m + 1, r);
            count += merge(data, l, m, r);
            return count;
        }

        public int merge(int[] data, int l, int m, int r) {
            int p1 = l;
            int p2 = m + 1;
            int[] tmp = new int[r - l + 1];
            int i = 0;
            int count = 0;
            int windowR = m + 1;
            for (int j = l; j <= m; j++) {
                while (windowR <= r && (data[j] >> 1) > (data[windowR])) {
                    windowR++;
                }
                count += windowR - m - 1;
            }
            while (p1 <= m && p2 <= r) {

                tmp[i++] = data[p1] < data[p2] ? data[p1++] : data[p2++];
            }
            while (p1 <= m) {
                tmp[i++] = data[p1++];
            }
            while (p2 <= r) {
                tmp[i++] = data[p2++];
            }
            for (i = 0; i < tmp.length; i++) {
                data[l + i] = tmp[i];
            }
            return count;
        }
    }

    /**
     * 给定一个数组，和最大值、最小值，
     * 数组中有多少个[子数组]累加的和在[low,high]内
     * 解法：
     * 1.
     */
    @AlgName("滑动范围子数组")
    public static class CountOfRangeSum extends AlgCompImpl<Integer, Triple<ArrayList<Integer>, Integer, Integer>> {

        RecLeftMinSum recursionMergeSort = new RecLeftMinSum();

        /**
         * @return tripple
         * left:array
         * middle:begin
         * right:end
         */
        @Override
        public Triple<ArrayList<Integer>, Integer, Integer> prepare() {
//            int dataSize = ThreadLocalRandom.current().nextInt(40000, 50000);
//            int[] data = new int[dataSize];
//            for (int i = 0; i < dataSize; i++) {
//                data[i] = ThreadLocalRandom.current().nextInt();
//            }
//
//            ArrayList<Integer> arr = (ArrayList<Integer>) Arrays.stream(data).boxed().collect(Collectors.toList());
//            Integer low = ThreadLocalRandom.current().nextInt();
//            Integer high = ThreadLocalRandom.current().nextInt(low, arr.size() + low);
//            return Triple.of(arr, low, high);

//            int[] data = {563556667, -1012524672, 1491501694,
//                    -1480033966, -1245638535, 2066768042, 1311799760, -1002980351, 1165722586, 347603321, 1548606054, -429233150, 483901029, -1674966169, 453933335, -2007978434, 883343438, 1602532740, -815363369, -302414435, -953689218, -925066766, 1585562704, 1339022962, -42086774, -1363852043, 1328596694, 1137222683, -592860060, -1433611176, -1286786609, -952924514, 368426232, 1077959161, -1877208063, -898417097, -1028185367, 1119330546, -785119462, 546546327, -2043038289, -1900204147, 1467850904, -571711076, 1335137205, -1774979619, -1742744172, -671770763, -354188900, 442553898, 2081402726, 1115090527, 806984929, 1463365979, 1620875703, -122337266, -538230169, 1629058707, 75450913, 1612527293, -150374364, 207780925, -232683157, 2098878355, -2144826050, -819912306, -6857103, 780106383, -987191123, 113196052, -530409641, 518230758, -19129718, 14757680, 1275436208, 1533176182, 884632623, 1314465473, 694109091, -1044720953, -347687316, -1415160582, -1723663805, 15664708, -614404150, 371620671, -371368061, 1581393389, 940733566, -19306217, 766793112, 1126596070, -1047896854, 1060643081, 2111875557, 1598727998, 1197959422, 1457517179, 1626957177, 2126324412, 1461743860, -535209275, -735647504, -2078471429, 32728664, -1557676871, -889901556, 454161634, -1634654574, 580820166, 42328056, 1934302677, -2053538000, -900195754, -1970715524, 89942525, -27110073, 436847576, 1798084974, -942338377, 1347712673, -427372128, -768970558, 1843239241, 1991772114, -1037485502, -537378856, -1399945837, -1021056824, 1440408402, -764303578, -205977296, 2040077216, 1980445834, 1666589930, -1287015674, 1148403905, -970663161, 157237225, -1799865267, -1049408699, 1596211648, -1660090972, -917408970, 268212934, 1252476241, 1742774680, -1202796628, -1905471044, -1492508271, 1046834146, 1965741427, -1943460599, 1269662716, 17217307, 177269663, 2102826878, -628720057, -919574364, 1347738257, -1479610737, 1714663662, 1433960373, 177813241, -1557914413, 1473343448, 1886678784, 928569939, 238910796, -1984810277, -246530644, 1411631068, -1288904717, 651236519, -890351159, -2074451080, 1797114381, -881700967, -31911615, 189861352, -95877518, 173080968, 912079313, -53423486, -1412336576, -776730212, 313952029, -70229776, -481342271, -1904528967, -359408104, -1984869542, -321862995, 1574146684, 815813504, -1591233294, 1014942026, -900636278, -187328817, 795591310, 1179524648, -286826089, -1174288881, 1218445015, 413863307, -1781418278, 2091350626, -876044357, -1227359796, 726847839, 897058015, 1788142702, -931550277, -2043554344, 179052395, 1305943139, -1488483336, 1455757605, -1433414291, -980150670, -532986838, -989373349, 1576889231, 1908473735, 1240066170, -2136197556, 863246224, 1457066593, 1915736733, 1903508739, -1920466625, 652737917, 1970598104, 1508118002, -1345902130, -1811122522, 1107513966, 23239511, -2019872883, -760161509, -224418077, 1015410631, -348721708, -1431769523, -863106318, 522814908, -1908283455, -1664047807, -807261325, 255485005, -2139238534, 85086183, -1946196540, 1997433280, 503707598, -1061858319, 1447515929, 2051073622, 1403323062, -1911181533, 956895968, 1623023782, 668226704, 1336227685, 694258457, -494960125, -1077174239, 2108253480, -1086329246, 1619559193, -1829927884, 495979654, 1384240719, 1897655019, -1441012507, -907647742, 1820217979, -1201965520, 984383511, 609051771, 302809175, 2104828197, 382437737, -231472746, -1379372476, -839324821, -345312990, 1060846486, 1872764091, 309643863, -824510144, -542138562, -2073832427, -719707044, 809482416, 1173176663, -1765501751, -1826329400, 1286288471, 1614464256, 960502911, -1714857658, -1353947418, -91745705, 1478456889, -1133417101, 1300291433, 2048798015, 424747555, 1162535854, 9625551, 1180140581, 1084570098, 1236582782, -2128293868, 1723641915, 417532479, -1529568072, -944939695, -799580348, 1896480862, -381543849, 660566803, -1015443400, -1212408945, 48339205, 492657402, -904585574, -1922286549, -1594131860, 1975678545, -1427104258, 1118755637, -82202356, -1254034256, -561331735, 1191917560, -1093088033, -1876251125, 1075493909, -573421421, 1012803694, 1282618276, 217940538, -1319322413, 1939869780, 1914411155, -208447934, -112381409, -437784396, -3818997, 79389101, 1757558389, 541338492, 120254164, 1835867505, 868684662, -739614326, -1281114491, -1733277322, 888179337, 1504875287, -830634507, 805745501, -967823709, 1144768650, 721764851, 850019738, 1958905751, 266555791, 1135132306, -1636593579, 1859590336, -444376811, -1297090556, 124298115, -558574512, 1278518199, -634051980, 916940362, -376545359, 925196744, -86484134, -2100869504, -1979975816, 1383122704, 1869233446, 933976259, 1137860817, -1929450619, -1544397900, -1576358934, -1387490380, 1853787047, 163315286, 2093693491, -155290452, -591830865, 1858605114, 309483932, 1866780202, -163876636, 142755506, -448345502, 1893187587, 1873726177, 618316870, -442378907, 1498961371, -1863249118, 1389232602, -175774642, 2135141794, 1124594855, -98974716, 1903940379, -1404589014, 2009772682, -913560137, 2006758090, 143450796, -586932346, 1741659920, 846714208, -1814642654, -1068469329, 57490496, 1043071230, -1838071926, 575610102, -1564348219, -1473022744, 571925595, -1808047602, 87669728, -648996067, 871379552, 301294482, -1427877000, 329591424, -2050071643, 1561217346, 1094085208, -268351352, -1527755904, 569396008, -793718928, 110921563, -1589315937, -557053715, 783444268, -988405111, -433058407, 332702761, 1963251734, -792722747, 370586185, -1295588212, 307321019, 2026491229};

//            int [] data = {1,-1,0};

//            int low = 1;
//            int high = 1332657759;

            /**
             * [0]
             * 0
             * 0
             */
            /*
            int [] data = {-2,5,-1};
            int low = -2;
            int high = 2;
             */
            /**
             * [-2147483647,0,-2147483647,2147483647]
             * -564
             * 3864
             */
            int [] data = {-2147483647,0,-2147483647,2147483647};
            int low = -564;
            int high = 3864;
            ArrayList<Integer> arr = (ArrayList<Integer>) Arrays.stream(data).boxed().collect(Collectors.toList());
            return Triple.of(arr, low, high);








        }

        /**
         * @param data left:array
         *             middle:begin
         *             right:end
         * @return 在窗口内的累加子数组
         */
        @Override
        protected Integer standard(Triple<ArrayList<Integer>, Integer, Integer> data) {

            int[] arr = data.getLeft().stream().mapToInt(a -> a).toArray();
            int low = data.getMiddle();
            int high = data.getRight();
            int result = 0;
            if (arr == null || arr.length < 1) {
                return 0;
            }
            for (int i = 0; i < arr.length; i++) {
                int step = 0;
                while (step < arr.length){

                        int[] subArray = ArrayUtils.subarray(arr, i, i+step);
                        long sum = Arrays.stream(subArray).sum();
                        if (sum >= low && sum <= high && subArray.length > 0) {
                            log.debug("子数组:[{}],SUM:{}", subArray, sum);
                            result++;
                        }

                    step++;
                }
            }
            return result;

//            int[] arr = data.getLeft().stream().mapToInt(a -> a).toArray();
//            int low = data.getMiddle();
//            int high = data.getRight();
//            int[] accumulateArray = new int[arr.length];
//            accumulateArray[0] = arr[0];
//            for (int i = 1; i < arr.length; i++) {
//                accumulateArray[i] = accumulateArray[i - 1] + arr[i];
//            }
//            int result = mergeSort(accumulateArray,low,high);
//            log.info("结果：{}",accumulateArray);
//            return result;
        }

        /**
         * 先做一遍累加和数组
         * 根据累加和的特性，A-B区间的和=(0-A)的和减去(0-B)的和。
         * merge时，右侧数据根据滑动窗口检索左侧符合范围的数据
         *
         * @param data left:array
         *             middle:begin
         *             right:end
         * @return 在窗口内的累加子数组
         */

        @Override
        protected Integer test(Triple<ArrayList<Integer>, Integer, Integer> data) {
            int[] arr = data.getLeft().stream().mapToInt(a -> a).toArray();
            int low = data.getMiddle();
            int high = data.getRight();
            long [] accumulateArray = new long[arr.length];
            accumulateArray[0] = arr[0];
            for (int i = 1; i < arr.length; i++) {
                accumulateArray[i] = accumulateArray[i - 1] + arr[i];
            }
//            int count = mergeSort(accumulateArray,low,high);
            int count = recMergeSort(accumulateArray, 0,
                    accumulateArray.length - 1, low, high);

                        log.info("结果：{}",accumulateArray);
            return count;
        }

        /**
         * 递归版本
         *
         * @param accumulateArray
         * @param left
         * @param right
         * @param windowL
         * @param windowR
         * @return
         */
        public int recMergeSort(long[] accumulateArray, int left, int right, int windowL, int windowR) {
            int count = 0;
            if (accumulateArray == null) {
                return count;
            }
            if (accumulateArray.length < 2) {
                return accumulateArray[0] <= windowR && accumulateArray[0] >= windowL ? 1 : 0;
            }
            if (left == right) {
                return accumulateArray[left] <= windowR && accumulateArray[left] >= windowL ? 1 : 0;
            }
            int M = left + ((right - left) >> 1);
            count += recMergeSort(accumulateArray, left, M, windowL, windowR);
            count += recMergeSort(accumulateArray, M + 1, right, windowL, windowR);
            count += merge(accumulateArray, left, M, right, windowL, windowR);
            return count;
        }

        /**
         * 非递归版本
         *
         * @param accumulateArray
         * @param windowL
         * @param windowR
         * @return
         */
        public int mergeSort(long[] accumulateArray, int windowL, int windowR) {
            int N = accumulateArray.length;
            int step = 1;
            int count = 0;
            while (step <= N){
                int L = 0;
                while (L<=N){
                    int M = L + step;
                    if(M >= N){
                        break;
                    }
                    int R = Math.min(M+step,N-1);
                    count += merge(accumulateArray,L,M,R,windowL,windowR);
                    L = R+1;
                }
                if(step > N/2){
                    break;
                }
                step <<= 1;
            }
            return count;

        }

        public int merge(long[] accumulateArray, int l, int m, int r, int lower,
                         int high) {
            int i = 0;
            int p1 = l;
            int p2 = m + 1;
            int count = 0;
            int windowL = l;
            int windowR = l;
            long[] tmp = new long[r - l + 1];
            for (int j = m + 1; j <= r; j++) {
                long min = accumulateArray[j]-high;
                long max = accumulateArray[j]-lower;
                while (windowR <= m && accumulateArray[windowR]<=max){
                    windowR++;
                }
                while (windowL <=m && accumulateArray[windowL] <=min){
                    windowL++;
                }
                count += windowR-windowL;

            }
            while (p1 <= m && p2 <= r) {
                tmp[i++] = accumulateArray[p1] < accumulateArray[p2] ? accumulateArray[p1++] : accumulateArray[p2++];
            }
            while (p1 <= m) {
                tmp[i++] = accumulateArray[p1++];
            }
            while (p2 <= r) {
                tmp[i++] = accumulateArray[p2++];
            }
            for (i = 0; i < tmp.length; i++) {
                accumulateArray[l + i] = tmp[i];
            }
            return count;
        }
    }


}
