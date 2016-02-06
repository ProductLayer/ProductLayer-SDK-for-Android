/*
 * Copyright (c) 2015, ProductLayer GmbH All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.productlayer.android.common.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.productlayer.android.common.R;
import com.productlayer.android.common.adapter.BrandAdapter;
import com.productlayer.android.common.adapter.BrandOwnerAdapter;
import com.productlayer.android.common.dialog.CategorySelectionDialogFragment;
import com.productlayer.android.common.dialog.NewProductDialogFragment;
import com.productlayer.android.common.global.LoadingIndicator;
import com.productlayer.android.common.global.ObjectCache;
import com.productlayer.android.common.handler.AppBarHandler;
import com.productlayer.android.common.handler.DataChangeListener;
import com.productlayer.android.common.handler.HasAppBarHandler;
import com.productlayer.android.common.handler.HasPLYAndroidHolder;
import com.productlayer.android.common.handler.PLYAndroidHolder;
import com.productlayer.android.common.model.CategoryListItem;
import com.productlayer.android.common.model.SimpleBrand;
import com.productlayer.android.common.util.BitmapUtil;
import com.productlayer.android.common.util.CameraUtil;
import com.productlayer.android.common.util.LocaleUtil;
import com.productlayer.android.common.util.MetricsUtil;
import com.productlayer.android.common.util.PhotoUtil;
import com.productlayer.android.common.util.PicassoTarget;
import com.productlayer.android.common.util.SnackbarUtil;
import com.productlayer.android.common.view.FocusAutoCompleteTextView;
import com.productlayer.android.sdk.PLYAndroid;
import com.productlayer.android.sdk.PLYCompletion;
import com.productlayer.android.sdk.services.ImageService;
import com.productlayer.android.sdk.services.ProductService;
import com.productlayer.core.beans.BrandOwner;
import com.productlayer.core.beans.Category;
import com.productlayer.core.beans.Product;
import com.productlayer.core.beans.ProductImage;
import com.productlayer.core.error.PLYStatusCodes;
import com.productlayer.core.utils.GTINValidator;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Updates an existing product or guides the user through the creation of a new product.
 *
 * Requires the activity to implement {@link HasAppBarHandler}, {@link HasPLYAndroidHolder}.
 */
public class ProductEditFragment extends NamedFragment {

    public static final String NAME = "ProductEdit";

    private static final String KEY_GTIN = "gtin";
    private static final String KEY_PRODUCT = "product";
    private static final String STATE_CATEGORY_KEY = "categoryKey";
    private static final String STATE_PRODUCT = "product";

    private AppBarHandler appBarHandler;

    private PLYAndroid client;
    private PLYAndroid sequentialClient;

    private CategoryListItem[] categories;
    private Category[] suggestedCategories;
    private BrandOwner[] suggestedBrandOwners;
    private boolean allBrandsLoaded = false;
    private boolean allBrandOwnersLoaded = false;

    private boolean isNewProduct;
    private Product dbProduct;

    private String gtin;
    private String language;
    private String selectedCategoryKey;

    private ImageView productImage;
    private EditText productName;
    private FocusAutoCompleteTextView brand;
    private FocusAutoCompleteTextView brandOwner;
    private Button selectCategory;
    private Target productImageTarget;

    /**
     * Constructs a new instance with the specified parameters. The parameters passed this way survive
     * recreation of the fragment due to orientation changes etc.
     *
     * This is called when a new product is to be created.
     *
     * @param gtin
     *         the GTIN of the new product
     * @return the fragment with the given parameters
     */
    public static ProductEditFragment newInstance(String gtin) {
        ProductEditFragment productEditFragment = new ProductEditFragment();
        Bundle args = new Bundle();
        args.putString(KEY_GTIN, gtin);
        productEditFragment.setArguments(args);
        return productEditFragment;
    }

    /**
     * Constructs a new instance with the specified parameters. The parameters passed this way survive
     * recreation of the fragment due to orientation changes etc.
     *
     * This is called when an existing product is to be updated.
     *
     * @param product
     *         the product to update
     * @return the fragment with the given parameters
     */
    public static ProductEditFragment newInstance(@SuppressWarnings("TypeMayBeWeakened") Product product) {
        ProductEditFragment productEditFragment = new ProductEditFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_PRODUCT, product);
        productEditFragment.setArguments(args);
        return productEditFragment;
    }

    /**
     * @param gtin
     *         the GTIN of the new product or of the product to update
     * @return this fragment's name and initialization parameters
     */
    public static String makeInstanceName(String gtin) {
        String productParam = gtin == null ? "" : gtin;
        return NAME + "(" + productParam + ")";
    }

    @Override
    public String getInstanceName() {
        return makeInstanceName(gtin);
    }

    @Override
    public FragmentGrouping getGrouping() {
        return FragmentGrouping.NONE;
    }

    // FRAGMENT LIFECYCLE - START //

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = getActivity();
        try {
            appBarHandler = ((HasAppBarHandler) activity).getAppBarHandler();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity + " must implement HasAppBarHandler");
        }
        PLYAndroidHolder plyAndroidHolder;
        try {
            plyAndroidHolder = ((HasPLYAndroidHolder) activity).getPLYAndroidHolder();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity + " must implement HasPLYAndroidHolder");
        }
        client = plyAndroidHolder.getPLYAndroid();
        if (client == null) {
            throw new RuntimeException("PLYAndroid must bet set before creating fragment " + this);
        }
        // execute some of the queries sequentially (i.e. creating product, uploading image and info)
        sequentialClient = client.copyForOrderedThreadExecution();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        language = LocaleUtil.getKeyboardLanguage(getActivity());
        Log.d(getClass().getSimpleName(), "Language is set to " + language);
        Bundle args = getArguments();
        gtin = args.getString(KEY_GTIN);
        isNewProduct = gtin != null;
        if (savedInstanceState != null) {
            selectedCategoryKey = savedInstanceState.getString(STATE_CATEGORY_KEY);
            dbProduct = (Product) savedInstanceState.getSerializable(STATE_PRODUCT);
        } else {
            if (!isNewProduct) {
                dbProduct = (Product) args.getSerializable(KEY_PRODUCT);
            }
        }
        if (!isNewProduct) {
            assert dbProduct != null;
            gtin = dbProduct.getGtin();
        }
        // this fragment adds actions to the app bar
        setHasOptionsMenu(true);
        // populate category list items
        new Thread(new Runnable() {
            @Override
            public void run() {
                Activity activity = getActivity();
                if (activity == null) {
                    return;
                }
                Category[] categoryArr = ObjectCache.getCategories(activity, client, false, false);
                if (categoryArr != null) {
                    categories = CategoryListItem.fromCategories(categoryArr);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setCategoryButtonTextFromKey();
                        }
                    });
                }
            }
        }).start();
        // retrieve suggested brands and brand owners
        ProductService.getBrandOwnerSuggestions(client, gtin, new PLYCompletion<BrandOwner[]>() {
            @Override
            public void onSuccess(BrandOwner[] result) {
                Log.d("GetBrandSuggCallback", "Retrieved " + result.length + " brand (owner) suggestions");
            }

            @Override
            public void onPostSuccess(BrandOwner[] result) {
                suggestedBrandOwners = result;
                addSuggestedBrands();
                addSuggestedBrandOwners();
            }

            @Override
            public void onError(PLYAndroid.QueryError error) {
                Log.d("GetBrandSuggCallback", error.getMessage());
            }
        });
        // retrieve suggested categories
        ProductService.getCategorySuggestions(client, gtin, new PLYCompletion<Category[]>() {
            @Override
            public void onSuccess(Category[] result) {
                Log.d("GetCatSuggCallback", "Retrieved " + result.length + " category suggestions");
                List<Category> catSuggestions = new ArrayList<Category>();
                for (Category c : result) {
                    if (!c.getKey().equals("pl-prod-cat-uncategorized")) {
                        catSuggestions.add(c);
                    }
                }
                suggestedCategories = catSuggestions.toArray(new Category[catSuggestions.size()]);
            }

            @Override
            public void onPostSuccess(Category[] result) {
                setCategoryButtonTextFromKey();
            }

            @Override
            public void onError(PLYAndroid.QueryError error) {
                Log.d("GetCatSuggCallback", error.getMessage());
            }
        });
        if (dbProduct == null) {
            // create or get product and use the possibly enriched result to prefill fields
            createEmptyOrGetProduct(gtin, language, true, false, null);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View layout = inflater.inflate(R.layout.fragment_product_edit, container, false);
        productImage = (ImageView) layout.findViewById(R.id.product_image);
        PhotoUtil.registerSelectorPopup(getActivity(), this, productImage);
        productName = (EditText) layout.findViewById(R.id.product_name);
        brand = (FocusAutoCompleteTextView) layout.findViewById(R.id.brand);
        brandOwner = (FocusAutoCompleteTextView) layout.findViewById(R.id.brand_owner);
        // set up onactionlistener to hide the keyboard after finishing input on brand owner
        brandOwner.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    Activity activity = getActivity();
                    if (activity == null) {
                        return false;
                    }
                    InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context
                            .INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
        selectCategory = (Button) layout.findViewById(R.id.select_category);
        // set up onclicklistener to open a dialog to act as category selection
        selectCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCategorySelection();
            }
        });
        if (!isNewProduct) {
            fillEmptyFields(dbProduct);
        }
        setCategoryButtonTextFromKey();
        appBarHandler.setEditProductAppBar(layout, isNewProduct ? getString(R.string.new_product) :
                dbProduct.getName());
        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (isNewProduct) {
            if (savedInstanceState == null) {
                // congratulate and ask the user to take a photo of the new product
                askTakePhoto();
            } else {
                Activity activity = getActivity();
                if (activity == null) {
                    return;
                }
                final File photoFile = new File(PhotoUtil.getPhotoPathCache(gtin, activity));
                if (photoFile.exists() && photoFile.isFile()) {
                    // if there is a cached photo for this gtin display it (wait for view dimensions to be
                    // known)
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loadProductImage(photoFile.getAbsolutePath(), productImage);
                        }
                    }, 100);
                }
            }
        } else {
            // if an existing product is edited, load the default image of the product if any
            final ProductImage defaultImage = dbProduct.getDefaultImage();
            if (defaultImage != null) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Activity activity = getActivity();
                        if (activity == null) {
                            return;
                        }
                        int imageWidthPx = productImage.getWidth();
                        // TODO height adjusted to screen
                        int imageHeightPx = MetricsUtil.inPx(128);
                        String productImageUrl = ImageService.getImageForSizeURL(client, defaultImage
                                .getImageFileId(), imageWidthPx, imageHeightPx, true, null);
                        productImageTarget = PicassoTarget.roundedCornersImage(activity, productImage);
                        Picasso.with(activity).load(productImageUrl).into(productImageTarget);
                    }
                }, 100);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // get all available brands and brand owners for autocompletion
        // done after everything is visible due to expensive nature of call if not cached
        if (!allBrandsLoaded) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Activity activity = getActivity();
                    if (activity == null) {
                        return;
                    }
                    ObjectCache.getBrands(activity, client, false, false);
                    allBrandsLoaded = true;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addSuggestedBrands();
                        }
                    });
                }
            }).start();
        }
        if (!allBrandOwnersLoaded) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Activity activity = getActivity();
                    if (activity == null) {
                        return;
                    }
                    ObjectCache.getBrandOwners(activity, client, false, false);
                    allBrandOwnersLoaded = true;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addSuggestedBrandOwners();
                        }
                    });
                }
            }).start();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_CATEGORY_KEY, selectedCategoryKey);
        outState.putSerializable(STATE_PRODUCT, dbProduct);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(getClass().getSimpleName(), "Received result code " + resultCode + " from request " +
                requestCode);
        Activity activity = getActivity();
        if (activity != null && PhotoUtil.onActivityResult(requestCode, resultCode, data, getContext(),
                PhotoUtil.getPhotoPathCache(gtin, activity))) {
            LoadingIndicator.show();
            // upload new product image (and create product if not yet created)
            uploadProductImage();
        } else if (requestCode == CategorySelectionDialogFragment.REQUEST_CODE_SELECT_CATEGORY) {
            // callback from category selection dialog
            CategoryListItem category = data.getParcelableExtra(CategorySelectionDialogFragment
                    .KEY_SELECTION);
            Log.d(getClass().getSimpleName(), "Selected category " + category);
            selectCategory.setText(category.toString());
            selectedCategoryKey = category.get(CategoryListItem.KEY_KEY).toString();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    // FRAGMENT LIFECYCLE - END //

    // MENU - START //

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.actions_save, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_save) {
            saveProduct();
            return true;
        }
        return false;
    }

    // MENU - END //

    /**
     * Creates the product in the ProductLayer database or retrieves it if it already exists. Only the GTIN
     * and the language of the product are known at this point but the server may enrich the product with
     * additional information.
     *
     * @param gtin
     *         the GTIN of the product to create or look up
     * @param language
     *         the language of the product to create or look up
     * @param useEnrichment
     *         whether to use the returned, possibly enriched, product information to prefill fields
     * @param promptForLogin
     *         whether to prompt for login if authorization fails
     * @param onPostSuccess
     *         a runnable to execute once the product is created or has been retrieved (possibly after a login
     *         prompt)
     */
    private void createEmptyOrGetProduct(final String gtin, final String language, final boolean
            useEnrichment, final boolean promptForLogin, final Runnable onPostSuccess) {
        Product product = new Product(gtin);
        product.setLanguage(language);
        ProductService.createProduct(sequentialClient, product, new PLYCompletion<Product>() {
            @Override
            public void onSuccess(Product result) {
                Log.d("CreateProductCallback", "New product with GTIN " + gtin + " (" + language + ") " +
                        "created");
            }

            @Override
            public void onPostSuccess(Product result) {
                dbProduct = result;
                if (useEnrichment) {
                    fillEmptyFields(result);
                }
                if (onPostSuccess != null) {
                    onPostSuccess.run();
                }
            }

            @Override
            public void onError(PLYAndroid.QueryError error) {
                Log.d("CreateProductCallback", error.getMessage());
                if (error.hasInternalErrorCode(PLYStatusCodes.NOT_INSERTED_DUPLICATE_FOUND_CODE)) {
                    // product has already been created - retrieve it
                    ProductService.getProductForGtin(sequentialClient, gtin, language, false, null, new
                            PLYCompletion<Product>() {
                        @Override
                        public void onSuccess(Product result) {
                            Log.d("GetProductCallback", "Got product with GTIN " + gtin + " (" + language +
                                    ")");
                        }

                        @Override
                        public void onPostSuccess(Product result) {
                            dbProduct = result;
                            if (useEnrichment) {
                                fillEmptyFields(result);
                            }
                            if (onPostSuccess != null) {
                                onPostSuccess.run();
                            }
                        }

                        @Override
                        public void onError(PLYAndroid.QueryError error) {
                            Log.d("GetProductCallback", error.getMessage());
                            SnackbarUtil.make(getActivity(), getView(), error.getMessage(), Snackbar
                                    .LENGTH_LONG).show();
                        }

                        @Override
                        public boolean promptForLogin() {
                            return promptForLogin;
                        }
                    });
                }
            }

            @Override
            public boolean promptForLogin() {
                return promptForLogin;
            }
        });
    }

    /**
     * Fills the input fields that have no content yet with data from {@code product}.
     *
     * @param product
     *         the product information to fill the fields with
     */
    private void fillEmptyFields(Product product) {
        String enrichment = "";
        String name = product.getName();
        String brandStr = product.getBrand();
        String brandOwnerStr = product.getBrandOwner();
        String categoryKey = product.getCategory();
        if (name != null && !name.isEmpty() && !GTINValidator.isValidGTIN(name)) {
            if (productName != null && productName.getText().toString().isEmpty()) {
                productName.setText(name);
                enrichment += " Name: " + name;
            }
        }
        if (brandStr != null && !brandStr.isEmpty()) {
            if (brand != null && brand.getText().toString().isEmpty()) {
                brand.setText(brandStr);
                enrichment += " Brand: " + brandStr;
            }
        }
        if (brandOwnerStr != null && !brandOwnerStr.isEmpty()) {
            if (brandOwner != null && brandOwner.getText().toString().isEmpty()) {
                brandOwner.setText(brandOwnerStr);
                enrichment += " Brand Owner: " + brandOwnerStr;
            }
        }
        if (categoryKey != null && !categoryKey.isEmpty()) {
            if (selectCategory != null && selectedCategoryKey == null) {
                selectedCategoryKey = categoryKey;
                setCategoryButtonTextFromKey();
                enrichment += " Category: " + categoryKey;
            }
        }
        if (isNewProduct && !enrichment.isEmpty()) {
            Log.d("CreateProductCallback", "Enriched GTIN " + gtin + " (" + language + ") with " +
                    "additional info." + enrichment);
        }
    }

    /**
     * Saves the product with details collected from the input fields, either creating it in the process or
     * updating an existing product in the database.
     */
    private void saveProduct() {
        Product product;
        String name = productName.getText().toString();
        if (name.isEmpty()) {
            productName.setError(getResources().getText(R.string.product_name_error));
            return;
        }
        String brandStr = brand.getText().toString();
        String brandOwnerStr = brandOwner.getText().toString();
        if (dbProduct == null) {
            product = new Product();
            product.setGtin(gtin);
            product.setLanguage(language);
        } else {
            product = dbProduct;
        }
        if (!name.isEmpty()) {
            product.setName(name);
        }
        if (!brandStr.isEmpty()) {
            product.setBrand(brandStr);
        }
        if (!brandOwnerStr.isEmpty()) {
            product.setBrandOwner(brandOwnerStr);
        }
        product.setCategory(selectedCategoryKey);
        LoadingIndicator.show();
        // TODO enable/disable save button
        PLYCompletion<Product> completion = new PLYCompletion<Product>() {
            @Override
            public void onSuccess(Product result) {
                Log.d("SavePDetailsCallback", "Product details saved");
                dbProduct = result;
                LoadingIndicator.hide();
                if (isNewProduct) {
                    SnackbarUtil.make(getActivity(), getView(), R.string.product_saved, Snackbar
                            .LENGTH_LONG).show();
                    DataChangeListener.productCreate(result);
                } else {
                    SnackbarUtil.make(getActivity(), getView(), R.string.product_edited, Snackbar
                            .LENGTH_LONG).show();
                    DataChangeListener.productUpdate(result);
                }
            }

            @Override
            public void onPostSuccess(Product result) {
                FragmentActivity activity = getActivity();
                if (activity != null) {
                    activity.onBackPressed();
                }
            }

            @Override
            public void onError(PLYAndroid.QueryError error) {
                Log.d("SavePDetailsCallback", error.getMessage());
                if (isNewProduct || !error.isHttpStatusError() || !error.hasInternalErrorCode
                        (PLYStatusCodes.OBJECT_NOT_UPDATED_NO_CHANGES_CODE)) {
                    SnackbarUtil.make(getActivity(), getView(), error.getMessage(), Snackbar.LENGTH_LONG)
                            .show();
                }
                LoadingIndicator.hide();
            }

            @Override
            public void onPostError(PLYAndroid.QueryError error) {
                if (!isNewProduct && error.isHttpStatusError() && error.hasInternalErrorCode(PLYStatusCodes
                        .OBJECT_NOT_UPDATED_NO_CHANGES_CODE)) {
                    FragmentActivity activity = getActivity();
                    if (activity != null) {
                        activity.onBackPressed();
                    }
                }
            }
        };
        if (dbProduct == null) {
            ProductService.createProduct(sequentialClient, product, completion);
        } else {
            ProductService.updateProduct(sequentialClient, product, completion);
        }
    }

    /**
     * Uploads an image for a product. Attempts to create the product first if it has not been created yet.
     */
    private void uploadProductImage() {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        final String imagePath = PhotoUtil.getPhotoPathCache(gtin, activity);
        Runnable uploadProductImage = new Runnable() {
            @Override
            public void run() {
                // TODO limit file size
                ImageService.uploadProductImage(sequentialClient, gtin, imagePath, new
                        PLYCompletion<ProductImage>() {
                    @Override
                    public void onSuccess(ProductImage result) {
                        Log.d("UploadPImageCallback", "New image for product with GTIN " + gtin + " " +
                                "uploaded");
                        LoadingIndicator.hide();
                        if (isNewProduct) {
                            SnackbarUtil.make(getActivity(), getView(), R.string.image_uploaded_more_info,
                                    Snackbar.LENGTH_LONG).show();
                        } else {
                            SnackbarUtil.make(getActivity(), getView(), R.string.image_uploaded, Snackbar
                                    .LENGTH_LONG).show();
                        }
                        DataChangeListener.imageCreate(result);
                    }

                    @Override
                    public void onPostSuccess(ProductImage result) {
                        loadProductImage(imagePath, productImage);
                    }

                    @Override
                    public void onError(PLYAndroid.QueryError error) {
                        Log.d("UploadPImageCallback", error.getMessage());
                        LoadingIndicator.hide();
                        SnackbarUtil.make(getActivity(), getView(), error.getMessage(), Snackbar
                                .LENGTH_LONG).show();
                    }
                });
            }
        };
        if (dbProduct == null) {
            createEmptyOrGetProduct(gtin, language, true, true, uploadProductImage);
        } else {
            uploadProductImage.run();
        }
    }

    /**
     * Loads an image from the local file system and displays it in an image view, scaled to the image view's
     * size.
     *
     * Must be run on the main thread once layouting has finished.
     *
     * @param imagePath
     *         the path to the image
     * @param imageView
     *         the view to display the image in
     */
    private void loadProductImage(String imagePath, ImageView imageView) {
        Bitmap bitmap = BitmapUtil.fromFileScaled(imagePath, imageView.getWidth(), imageView.getHeight(),
                true);
        if (bitmap == null) {
            Log.d(getClass().getSimpleName(), "Failed to load product image: image view dimensions not yet " +
                    "" + "determined");
        } else {
            Activity activity = getActivity();
            if (activity != null) {
                imageView.setImageDrawable(BitmapUtil.getRoundedBitmapDrawable(activity, bitmap));
            }
        }
    }

    /**
     * Adds brand suggestions to the respective auto-complete text fields.
     *
     * Must be run on the UI thread.
     */
    private void addSuggestedBrands() {
        Activity activity = getActivity();
        if (brand == null || activity == null) {
            return;
        }
        String[] allBrands = allBrandsLoaded ? ObjectCache.getBrands(activity, client, true, false) : null;
        BrandAdapter brandAdapter = new BrandAdapter(activity, R.layout.dropdown_brand_item,
                suggestedBrandOwners, allBrands);
        brand.setAdapter(brandAdapter);
        brand.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object object = parent.getItemAtPosition(position);
                String brandName;
                String brandOwnerName = null;
                if (object instanceof SimpleBrand) {
                    brandName = object.toString();
                    brandOwnerName = ((SimpleBrand) object).brandOwner;
                } else {
                    brandName = (String) object;
                }
                brand.setText(brandName);
                if (brandOwnerName != null && !brandOwnerName.isEmpty()) {
                    // also set the brand's owner
                    brandOwner.setText(brandOwnerName);
                }
            }
        });
    }

    /**
     * Adds brand owner suggestions to the respective auto-complete text fields.
     *
     * Must be run on the UI thread.
     */
    private void addSuggestedBrandOwners() {
        Activity activity = getActivity();
        if (brandOwner == null || activity == null) {
            return;
        }
        String[] allBrandOwners = allBrandOwnersLoaded ? ObjectCache.getBrandOwners(activity, client, true,
                false) : null;
        BrandOwnerAdapter brandOwnerAdapter = new BrandOwnerAdapter(activity, R.layout
                .dropdown_brand_owner_item, suggestedBrandOwners, allBrandOwners);
        brandOwner.setAdapter(brandOwnerAdapter);
    }

    /**
     * Creates and shows the category selection dialog fragment.
     */
    private void openCategorySelection() {
        CategorySelectionDialogFragment categorySelectionDialogFragment = CategorySelectionDialogFragment
                .newInstance(categories);
        categorySelectionDialogFragment.show(getChildFragmentManager(), "CategorySelectionDialogFragment");
    }

    /**
     * Sets the title of the "Select Category" button to the name of the category identified by the specified
     * key - provided a category key selection has been made and the categories list has been populated and
     * the layout has been inflated.
     *
     * If no category key selection has been made yet, attempts to get a key from the suggested categories for
     * the current GTIN.
     *
     * @see #setSuggestedCategory()
     */
    private void setCategoryButtonTextFromKey() {
        setSuggestedCategory();
        if (selectedCategoryKey != null && selectCategory != null) {
            String selectedCategoryName = CategoryListItem.getName(selectedCategoryKey, categories);
            if (selectedCategoryName != null) {
                selectCategory.setText(selectedCategoryName);
            }
        }
    }

    /**
     * Sets the title of the "Select Category" button (non-localized) and the selected category key to the
     * first of any suggested categories - provided categories were suggested for the GTIN and no category has
     * yet been selected.
     */
    private void setSuggestedCategory() {
        if (selectedCategoryKey != null) {
            return;
        }
        if (suggestedCategories != null && suggestedCategories.length != 0) {
            selectedCategoryKey = suggestedCategories[0].getKey();
            selectCategory.setText(suggestedCategories[0].getName());
        }
    }

    /**
     * Opens a dialog asking the user to take a photo of the product.
     */
    private void askTakePhoto() {
        Activity activity = getActivity();
        if (activity != null && CameraUtil.hasCameraAny(activity)) {
            new NewProductDialogFragment().show(getChildFragmentManager(), "NewProductDialogFragment");
        }
    }
}
