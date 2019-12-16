# simple-trajectory-compressor
A simple implementation of polyline encoding algorithm in Java.  

## Principles

Google Maps Platform Doc: [Encoded Polyline Algorithm Format](https://developers.google.com/maps/documentation/utilities/polylinealgorithm).  
My blog(简体中文): [轨迹压缩算法(Polyline encoding algorithm)探究](https://guanhonly.github.io/2019/09/05/PolylineEncoding/)

## Usage

1. Create an object trjCompressor:

```java
TrjCompressor trjCompressor = new TrjCompressor(6);
```

The parameter of the constructor is the precision of this compressor. For example, if the parameter is 6, then only 6 decimal places are reserved for floating point numbers.

2. Encode `List<PointTs>` to a single string:

```java
String encoding = trjCompressor.encode(points); //type of points is List<PointTs>
```

3. Decode the compressed code to `List<PointTs>`:

```java
List<PointTs> decoding = trjCompressor.decode(trjCode) //type of trjCode is String
```

## Performance
Since it's a compression algorithm, I did a simple experiment to calculate the compression rate(compared to json format).   
The trajectory data of the experiment is downloaded from [Microsoft GeoLife](https://www.microsoft.com/en-us/download/details.aspx?id=52367)
and 171 trajectories are used. The code of this experiment is in the file `TrjCompressorTest.java`, you can change the
value of `path` which contains the trajectory data from GeoLife if you want to do the similar experiment. Here is the 
result of the experiment:
```text
mean of compression rate: 10.811463326040565 %
median of compression rate: 10.643147359032803 %
```
The mean and median of compression rates is about 10% in this experiment. It can be concluded that, polyline encoding
algorithm is an excellent compression algorithm for trajectory data.
