# 结论

## 顺序访问
+ array最快
+ arraylist 其次
+ linkedlist 最慢

```
Benchmark                                                Mode     Cnt     Score      Error  Units
TImeComplex.arrayForEach                               sample  276969     0.036 ±    0.001  ms/op
TImeComplex.arrayForEach:arrayForEach·p0.00            sample             0.001             ms/op
TImeComplex.arrayForEach:arrayForEach·p0.50            sample             0.031             ms/op
TImeComplex.arrayForEach:arrayForEach·p0.90            sample             0.049             ms/op
TImeComplex.arrayForEach:arrayForEach·p0.95            sample             0.061             ms/op
TImeComplex.arrayForEach:arrayForEach·p0.99            sample             0.110             ms/op
TImeComplex.arrayForEach:arrayForEach·p0.999           sample             0.156             ms/op
TImeComplex.arrayForEach:arrayForEach·p0.9999          sample             0.197             ms/op
TImeComplex.arrayForEach:arrayForEach·p1.00            sample             0.279             ms/op
TImeComplex.arrayListForEach                           sample  109947     0.182 ±    0.002  ms/op
TImeComplex.arrayListForEach:arrayListForEach·p0.00    sample             0.110             ms/op
TImeComplex.arrayListForEach:arrayListForEach·p0.50    sample             0.141             ms/op
TImeComplex.arrayListForEach:arrayListForEach·p0.90    sample             0.202             ms/op
TImeComplex.arrayListForEach:arrayListForEach·p0.95    sample             0.270             ms/op
TImeComplex.arrayListForEach:arrayListForEach·p0.99    sample             1.512             ms/op
TImeComplex.arrayListForEach:arrayListForEach·p0.999   sample             3.232             ms/op
TImeComplex.arrayListForEach:arrayListForEach·p0.9999  sample             3.965             ms/op
TImeComplex.arrayListForEach:arrayListForEach·p1.00    sample             8.389             ms/op
TImeComplex.linkedForEach                              sample       4  7394.558 ± 7922.709  ms/op
TImeComplex.linkedForEach:linkedForEach·p0.00          sample          6652.166             ms/op
TImeComplex.linkedForEach:linkedForEach·p0.50          sample          6849.298             ms/op
TImeComplex.linkedForEach:linkedForEach·p0.90          sample          9227.469             ms/op
TImeComplex.linkedForEach:linkedForEach·p0.95          sample          9227.469             ms/op
TImeComplex.linkedForEach:linkedForEach·p0.99          sample          9227.469             ms/op
TImeComplex.linkedForEach:linkedForEach·p0.999         sample          9227.469             ms/op
TImeComplex.linkedForEach:linkedForEach·p0.9999        sample          9227.469             ms/op
TImeComplex.linkedForEach:linkedForEach·p1.00          sample          9227.469             ms/op
TImeComplex.linkedIteration                            sample   62629     0.319 ±    0.003  ms/op
TImeComplex.linkedIteration:linkedIteration·p0.00      sample             0.231             ms/op
TImeComplex.linkedIteration:linkedIteration·p0.50      sample             0.244             ms/op
TImeComplex.linkedIteration:linkedIteration·p0.90      sample             0.468             ms/op
TImeComplex.linkedIteration:linkedIteration·p0.95      sample             0.595             ms/op
TImeComplex.linkedIteration:linkedIteration·p0.99      sample             1.087             ms/op
TImeComplex.linkedIteration:linkedIteration·p0.999     sample             3.345             ms/op
TImeComplex.linkedIteration:linkedIteration·p0.9999    sample             4.381             ms/op
TImeComplex.linkedIteration:linkedIteration·p1.00      sample             5.235             ms/op
```
