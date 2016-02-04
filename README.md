# ProductLayer SDK for Android
![productlayer logo](https://prod.ly/images/logo_256x175.png)

The ultimate product information API, enabling a new breed of [product-centric apps](http://www.cocoanetics.com/2014/02/from-barcodes-to-productlayer/). This project contains the Android SDK. We also provide SDKs for [iOS](https://github.com/ProductLayer/ProductLayer-SDK-for-iOS) and [Java](https://github.com/ProductLayer/ProductLayer-SDK-for-Java).

See ProductLayer in action in our [prod.ly app](https://play.google.com/store/apps/details?id=com.productlayer.prodly).

## Usage

#### Grade
```
compile 'com.productlayer.ply-android-sdk:0.5.0'
compile 'com.productlayer.ply-android-common:0.5.0'
```

#### Maven
```
<dependency>
    <groupId>com.productlayer</groupId>
    <artifactId>ply-android-sdk</artifactId>
    <version>0.5.0</version>
</dependency>
<dependency>
    <groupId>com.productlayer</groupId>
    <artifactId>ply-android-common</artifactId>
    <version>0.5.0</version>
</dependency>
```

#### Get your API key from [developer.productlayer.com](https://developer.productlayer.com)
```java
PLYRestClientConfig config = new PLYRestClientConfig();
config.apiKey = "YOUR_API_KEY";
```

#### Create the PLYAndroid client
```java
PLYAndroid client = new PLYAndroid(config);
```

#### Make an API call
```java
ProductService.searchProductsByBrand(this, "Apple", null, null,
    new PLYCompletion<Product[]>() {
        @Override
        public void onSuccess(Product[] result) {
            // display results
        }

        @Override
        public void onError(QueryError error) {
        }
    }
);
```

This will run asynchronously, querying our API for all products by brand *Apple*.

To view the full functionality of our API please visit [developer.productlayer.com](https://developer.productlayer.com).
