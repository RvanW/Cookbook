package nl.mprog.robbert.cookbook;


import android.app.AlertDialog;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.inthecheesefactory.thecheeselibrary.fragment.support.v4.app.StatedFragment;
import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.ParseImageView;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Objects;



public class AddRecipeFragment extends StatedFragment implements View.OnClickListener, View.OnTouchListener {
    private static final int SELECT_FILE = 1;
    private static final int REQUEST_CAMERA = 0;
    private Recipe mRecipe;
    private ParseImageView imageView;

    private View view;

    public AddRecipeFragment() {
        // required empty constructor
    }

    public static AddRecipeFragment newInstance(Recipe recipe) {
        Bundle args = new Bundle();
        args.putSerializable("recipe",recipe);
        AddRecipeFragment fragment = new AddRecipeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    // these functions make sure the image stays visible after screen rotations
    @Override
    protected void onSaveState(Bundle outState) {
        super.onSaveState(outState);
        outState.putSerializable("recipe", mRecipe);
    }

    @Override
    protected void onRestoreState(Bundle savedInstanceState) {
        super.onRestoreState(savedInstanceState);
        // For example:
        mRecipe = (Recipe) savedInstanceState.getSerializable("recipe");
        assert mRecipe != null;
        if (mRecipe.getImageFile() != null) {
            imageView.setParseFile(mRecipe.getImageFile());
            imageView.loadInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    imageView.setVisibility(View.VISIBLE);
                }
            });
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRecipe = (Recipe) getArguments().getSerializable("recipe");
            // get the recipe online and update if possible
            ParseQuery<ParseObject> query = new ParseQuery<>(
                    "Recipe");
            query.whereEqualTo("objectId",mRecipe.getObjectId());
            query.getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    if (e==null) {
                        mRecipe = (Recipe) object;
                        if (mRecipe.getAuthor() == ParseUser.getCurrentUser()) {
                            setHasOptionsMenu(true);
                            getActivity().setTitle("Edit recipe");
                        }
                    }
                    else {
                        Log.d("EditRecipeFragment",e.getMessage());
                    }
                }
            });
        }
        else {
            mRecipe = new Recipe();
        }
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_edit_recipe, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle action bar item selection
        switch (item.getItemId()) {
            case R.id.delete_item:
                deleteDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete recipe")
                .setMessage("Are you sure you wish to delete everything associated with this recipe? \n Warning: this is action is irreversible!");
        // Add the buttons
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mRecipe.deleteInBackground(new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e==null) {
                            Fragment fragment = new MyRecipesFragment();
                            // Insert the fragment by replacing any existing fragment
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            fragmentManager.beginTransaction()
                                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                                    .replace(R.id.frag_holder, fragment)
                                    .commit();
                        }
                    }
                });
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_recipe, container, false);
        // set click listener for save button
        Button saveButton = (Button) view.findViewById(R.id.save);
        saveButton.setOnClickListener(this);
        getActivity().setTitle("New recipe");
        NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(1).setChecked(true);

        // set the navigation icon
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        Drawable nav_logo = getResources().getDrawable(R.drawable.ic_menu_camera, null);
        if (nav_logo != null) {
            int color = Color.parseColor("#FFFFFF");
            nav_logo.setTint(color);
            toolbar.setLogo(nav_logo);
        }
        if (mRecipe != null) {
            EditText title = (EditText) view.findViewById(R.id.newTitel);
            title.setText(mRecipe.getTitle());
            EditText description = (EditText) view.findViewById(R.id.newDescription);
            description.setText(mRecipe.getDescription());
            if (mRecipe.getPublic()) {
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.savePublic);
                checkBox.setChecked(true);
            }

            // check if there are ingredients to be listed
            if (mRecipe.getIngredients() != null) {
                String htmlString = "";
                for (String ingredient : mRecipe.getIngredients()) {
                    htmlString += ingredient + "\n";
                }
                if (!Objects.equals(htmlString, "")) {
                    TextView ingredients = (TextView) view.findViewById(R.id.newIngredients);
                    ingredients.setText(htmlString);
                }
            }

            // set the imageview if there's an image
            imageView = (ParseImageView) view.findViewById(R.id.imageUpload);
            if (mRecipe.getImageFile() != null) {
                imageView.setParseFile(mRecipe.getImageFile());
                imageView.loadInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] data, ParseException e) {
                        imageView.setVisibility(View.VISIBLE);
                    }
                });
            }
            else {
                imageView.setImageResource(R.drawable.ic_menu_camera);
            }
        }
        //set the ontouch listener so user may upload an image
        imageView.setOnTouchListener(this);

        Log.d("AddRecipeFragment: ","OnCreateView");
        return view;
    }

    @Override
    public void onClick(View v) {
        EditText title = (EditText) view.findViewById(R.id.newTitel);

        if (!Objects.equals(title.getText().toString().trim(), "")) {
            mRecipe.setTitle(title.getText().toString());

            EditText description = (EditText) view.findViewById(R.id.newDescription);
            mRecipe.setDescription(description.getText().toString());

            EditText ingredients = (EditText) view.findViewById(R.id.newIngredients);
            String[] ingredientString = ingredients.getText().toString().split("[\\r\\n]+");
            mRecipe.setIngredients(Arrays.asList(ingredientString));

            CheckBox savePublic = (CheckBox) view.findViewById(R.id.savePublic);
            mRecipe.setPublic(savePublic.isChecked());
            mRecipe.setAuthor(ParseUser.getCurrentUser());

            // Save this new recipe and return
            mRecipe.saveInBackground(new SaveCallback() {

                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Fragment fragment = new MyRecipesFragment();
                        // Insert the fragment by replacing any existing fragment
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                                .replace(R.id.frag_holder, fragment)
                                .commit();

                    } else {
                        Toast.makeText(
                                getActivity().getApplicationContext(),
                                "Error saving: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }

            });


        }
        else {
            Toast.makeText(getActivity(),"Please fill in a valid title.",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                // Create a button like response filter when touched
                imageView.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                imageView.invalidate();
                break;
            }
            case MotionEvent.ACTION_UP: {
                imageDialog();
            }
            case MotionEvent.ACTION_CANCEL: {
                //clear the overlay
                imageView.getDrawable().clearColorFilter();
                imageView.invalidate();
                break;
            }
        }

        return true;
    }

    private void imageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Upload an image..");
        CharSequence options[] = new CharSequence[] {"Camera","From device"};

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int pos) {
                switch (pos) {
                    case 0: // camera
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, REQUEST_CAMERA);
                        break;
                    case 1: // from gallery
                        intent = new Intent(
                                Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/*");
                        startActivityForResult(
                                Intent.createChooser(intent, "Select File"),
                                SELECT_FILE);
                        break;
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFile photo_parse_file = null;
        if (resultCode == MainActivity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                photo_parse_file = conversionBitmapParseFile(thumbnail);
            } else if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();
                String[] projection = {MediaStore.MediaColumns.DATA};
                CursorLoader cursorLoader = new CursorLoader(getActivity(), selectedImageUri, projection, null, null, null);
                Cursor cursor = cursorLoader.loadInBackground();
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();
                String selectedImagePath = cursor.getString(column_index);
                Bitmap bm;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(selectedImagePath, options);
                final int REQUIRED_SIZE = 200;
                int scale = 1;
                while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                        && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                    scale *= 2;
                options.inSampleSize = scale;
                options.inJustDecodeBounds = false;
                bm = BitmapFactory.decodeFile(selectedImagePath, options);
                photo_parse_file = conversionBitmapParseFile(bm);
            }
            if (photo_parse_file != null) {
                photo_parse_file.saveInBackground(new SaveCallback() {
                    public void done(ParseException e) {
                        if (e != null) {
                            Toast.makeText(getActivity(),
                                    "Error saving image: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(),
                                    "Succes!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                mRecipe.setImageFile(photo_parse_file);
                imageView.setParseFile(photo_parse_file);
                imageView.loadInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] data, ParseException e) {
                        imageView.setVisibility(View.VISIBLE);
                    }
                });
            }
        }
    }

    private ParseFile conversionBitmapParseFile(Bitmap imageBitmap){
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
        byte[] imageByte = byteArrayOutputStream.toByteArray();
        return new ParseFile("recipe_image.png",imageByte);
    }
}