# FlickrIt

FlickrIt lets you search images from Flickr API. It uses the Flickr API key.

<img src="./README_images/ic_flickr_logo.png" width="400" height="400"/>

### How FlickrIt search works?

- By default the app loads search results for the query "kittens".
- To search, you can use the `SearchView` inside the `Toolbar` on the top right corner.
- The app also lets you scroll endlessly.

### Architecture

FlickrIt is built on top of MVP architecture:

- The searching feature is fulfilled by a `SearchContract`.
- The `SearchInteractor` gets the photo results from the Flickr API via the FlickrApiClient. It contains the `SearchModel` which is the single source of truth of the app. The interactor publishes this model to the presenter
- The `SearchPresenter` talks to the `SearchInteractor`. It renders the `SearchState` (aka SearchViewModel) on the `SearchView` whenever the interactor publishes it.
- The `SearchView` renders the `SearchState` and also post actions to the `SearchPresenter`.

### Injections

- `FlickrApiClient` lets you use the Flickr search command and returns a `NetworkResults<SearchResponse>`.
- `ApiClient` is a wrapper on top of `OkHttpClient` which lets you make synchronous/asynchronous requests.
- `ImageService` lets you download images from network.
- `SearchMapper` and `PhotoMapper` helps transforming the network entities into DTOs.

### Unit Tests

- Unit tests can be found under `test` package.

### Image Caching

- The `ImageBitmapLoader` downloads and caches images using `MemoryBitmapCache` and `DiskBitmapCache` with given url.
- First, it checks `MemoryBitmapCache` and returns `Bitmap` if found.
- Second, it checks `DiskBitmapCache` and saves it in memory cache if found. Then, it returns the found `Bitmap`.
- Third, if the Bitmap wasn't found in either memory or disk, it downloads the image using `ImageDownloader` . Then, it saves the `Bitmap` to both the caches and returns it.

Thanks for stopping by! Have a great day :)


