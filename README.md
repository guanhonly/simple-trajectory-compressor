# SimpleTrajectoryCompressor
A simple implemention of polyline encoding algorithm in java.  
**The priciple of this algorithm:**  
Google Maps Platform Doc: [Encoded Polyline Algorithm Format](https://developers.google.com/maps/documentation/utilities/polylinealgorithm).  
My blog(简体中文): [轨迹压缩算法(Polyline encoding algorithm)探究](https://guanhonly.github.io/2019/09/05/PolylineEncoding/)

## Usage

A lossy compression to compress trajectory to a single string.

## How to use it

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
