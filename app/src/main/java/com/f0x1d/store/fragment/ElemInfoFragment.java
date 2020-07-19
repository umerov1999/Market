package com.f0x1d.store.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.f0x1d.store.App;
import com.f0x1d.store.BuildConfig;
import com.f0x1d.store.R;
import com.f0x1d.store.activity.MainActivity;
import com.f0x1d.store.db.Database;
import com.f0x1d.store.db.daos.ExtendedElementsDao;
import com.f0x1d.store.db.daos.MenuElementsDao;
import com.f0x1d.store.db.entities.ExtendedElement;
import com.f0x1d.store.db.entities.MenuElement;
import com.f0x1d.store.utils.FileUtils;
import com.f0x1d.store.view.CenteredToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ElemInfoFragment extends Fragment {
    public ExtendedItemsAdapter adapter;
    public MaterialButton addButton;
    public ImageView bigPhoto;
    public ExtendedFloatingActionButton changePhoto;
    public FloatingActionButton HomeBtn;
    public EditText description;
    public long elemId;
    public MenuElement element;
    public List<ExtendedElement> extendedElements = new ArrayList<>();
    public ExtendedElementsDao extendedElementsDao;
    public RecyclerView recyclerView;
    public CenteredToolbar toolbar;
    private String currentPhotoPath;

    public static ElemInfoFragment newInstance(long j) {
        Bundle bundle = new Bundle();
        bundle.putLong("id", j);
        ElemInfoFragment elemInfoFragment = new ElemInfoFragment();
        elemInfoFragment.setArguments(bundle);
        return elemInfoFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_elem_info, container, false);
        this.elemId = requireArguments().getLong("id");
        this.element = App.getInstance().getDatabase().menuElementsDao().getById(this.elemId);
        this.extendedElementsDao = App.getInstance().getDatabase().extendedElementsDao();
        this.HomeBtn = root.findViewById(R.id.home_button);
        this.HomeBtn.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).clearBackStack();
            ((MainActivity) requireActivity()).replaceFragment(ListFragment.newInstance(-1, getString(R.string.root)), "main_list", false, null);
        });

        this.changePhoto = root.findViewById(R.id.btn_change_photo);
        this.toolbar = root.findViewById(R.id.toolbar);
        this.recyclerView = root.findViewById(R.id.recycler_view);
        this.addButton = root.findViewById(R.id.add_btn);
        this.changePhoto.setOnClickListener(view1 -> {
            MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(requireActivity());
            materialAlertDialogBuilder.setItems(new CharSequence[]{getString(R.string.camera), getString(R.string.gallery)}, (dialogInterface, i) -> {
                if (i == 0) {
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
                        File file = null;
                        try {
                            file = createImageFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (file != null) {
                            Uri uriForFile = FileProvider.getUriForFile(requireActivity(), BuildConfig.APPLICATION_ID + ".fileprovider", file);
                            if (Build.VERSION.SDK_INT >= 24) {
                                intent.putExtra("output", uriForFile);
                                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            } else {
                                intent.putExtra("output", Uri.fromFile(file));
                            }
                            startActivityForResult(intent, 1337);
                        }
                    }
                } else if (i == 1) {
                    FileUtils.openImage("*/*", 228, ElemInfoFragment.this);
                }
            });
            materialAlertDialogBuilder.show();
        });
        this.toolbar.setTitle(this.element.name);
        this.toolbar.inflateMenu(R.menu.edit_item_menu);
        this.toolbar.getMenu().findItem(R.id.remove_description).setOnMenuItemClickListener(menuItem -> {
            description.setVisibility(View.GONE);
            SharedPreferences.Editor edit = App.getDefaultPreferences().edit();
            edit.putBoolean(elemId + "_desc", false).apply();
            return true;
        });
        this.addButton.setOnClickListener(v -> addButton());
        this.extendedElements.add(null);
        this.extendedElements.addAll(this.extendedElementsDao.getAll(this.elemId));
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 4);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            public int getSpanSize(int i) {
                return i == ExtendedItemsAdapter.HEADER ? 4 : 1;
            }
        });
        this.recyclerView.setLayoutManager(gridLayoutManager);
        RecyclerView recyclerView2 = this.recyclerView;
        ExtendedItemsAdapter extendedItemsAdapter = new ExtendedItemsAdapter();
        this.adapter = extendedItemsAdapter;
        recyclerView2.setAdapter(extendedItemsAdapter);
        new ItemTouchHelper(new ItemTouchHelper.Callback() {
            public boolean isItemViewSwipeEnabled() {
                return true;
            }

            public boolean isLongPressDragEnabled() {
                return false;
            }

            public boolean onMove(@NotNull RecyclerView recyclerView, @NotNull RecyclerView.ViewHolder viewHolder, @NotNull RecyclerView.ViewHolder viewHolder2) {
                return false;
            }

            public int getMovementFlags(@NotNull RecyclerView recyclerView, @NotNull RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof ExtendedItemsAdapter.EditTextViewHolder) {
                    return makeMovementFlags(0, 48);
                }
                return 0;
            }

            public void onSwiped(@NotNull final RecyclerView.ViewHolder viewHolder, int i) {
                final ExtendedElement work = extendedElements.get(viewHolder.getAdapterPosition());
                MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(requireActivity());
                materialAlertDialogBuilder.setCancelable(false);
                materialAlertDialogBuilder.setTitle(R.string.are_u_sure);
                materialAlertDialogBuilder.setMessage(R.string.what_would_u_like_do);
                materialAlertDialogBuilder.setNeutralButton(work.reserved ? R.string.clear_reserve : R.string.reserve, (dialogInterface1, i2) -> {
                    extendedElementsDao.updateReserved(!work.reserved, work.id);
                    work.reserved = !work.reserved;
                    adapter.notifyDataSetChanged();
                });
                materialAlertDialogBuilder.setPositiveButton(R.string.sell, (dialogInterface, i1) -> {
                    extendedElementsDao.delete(extendedElements.remove(viewHolder.getAdapterPosition()).id);
                    adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                });
                materialAlertDialogBuilder.setNegativeButton(R.string.cancel_action, (dialogInterface, i12) -> {
                    dialogInterface.cancel();
                    adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                });
                materialAlertDialogBuilder.show();
            }
        }).attachToRecyclerView(this.recyclerView);
        return root;
    }

    private void addButton() {
        ExtendedElement extendedElement = new ExtendedElement();
        extendedElement.ownerId = this.elemId;
        extendedElement.id = Database.getExtendedLastId() + 1;
        extendedElement.text = "";
        extendedElement.reserved = false;
        this.extendedElementsDao.insert(extendedElement);
        List<ExtendedElement> list = this.extendedElements;
        list.add(list.size(), extendedElement);
        ExtendedItemsAdapter extendedItemsAdapter = this.adapter;
        extendedItemsAdapter.notifyItemInserted(extendedItemsAdapter.getItemCount());
        this.recyclerView.scrollToPosition(this.adapter.getItemCount() - 1);
    }

    public File createImageFile() throws IOException {
        @SuppressLint("SimpleDateFormat") String str = "JPEG_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + "_";
        File file = new File(requireActivity().getFilesDir() + "/store_photos");
        if (!file.exists()) {
            file.mkdirs();
        }
        File file3 = new File(file, str + ".jpg");
        file3.createNewFile();
        this.currentPhotoPath = file3.getAbsolutePath();
        return file3;
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i2 == Activity.RESULT_OK) {
            if (i == 228) {
                File file = new File(requireActivity().getFilesDir() + "/store_photos");
                if (!file.exists()) {
                    file.mkdirs();
                }
                @SuppressLint("SimpleDateFormat") String str = "JPEG_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + "_";
                File file2 = new File(file, str + ".jpg");
                try {
                    FileUtils.copy(requireActivity().getContentResolver().openInputStream(intent.getData()), new FileOutputStream(file2));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                MenuElement remove = App.getInstance().getDatabase().menuElementsDao().getById(this.elemId);
                if (remove.imageSource != null && !remove.imageSource.isEmpty()) {
                    File vv = new File(remove.imageSource);
                    if (vv.exists()) {
                        vv.delete();
                    }
                }

                App.getInstance().getDatabase().menuElementsDao().updateImage(file2.getAbsolutePath(), this.elemId);
                Glide.with(this).load(file2).apply(new RequestOptions().centerCrop()).into(this.bigPhoto);
                this.bigPhoto.setVisibility(View.VISIBLE);
            }
            if (i == 1337) {
                File file3 = new File(this.currentPhotoPath);

                MenuElement remove = App.getInstance().getDatabase().menuElementsDao().getById(this.elemId);
                if (remove.imageSource != null && !remove.imageSource.isEmpty()) {
                    File vv = new File(remove.imageSource);
                    if (vv.exists()) {
                        vv.delete();
                    }
                }

                App.getInstance().getDatabase().menuElementsDao().updateImage(file3.getAbsolutePath(), this.elemId);
                Glide.with(this).load(file3).apply(new RequestOptions().centerCrop()).into(this.bigPhoto);
                this.bigPhoto.setVisibility(View.VISIBLE);
            }
        }
    }

    public class ExtendedItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        public static final int HEADER = 0;
        public static final int ITEM = 1;

        public ExtendedItemsAdapter() {
        }

        public int getItemViewType(int i) {
            return i == HEADER ? HEADER : ITEM;
        }

        @NotNull
        public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup viewGroup, int i) {
            if (i == HEADER) {
                return new BigPicViewHolder(getLayoutInflater().inflate(R.layout.item_big_pic, viewGroup, false));
            }
            if (i != ITEM) {
                return null;
            }
            return new EditTextViewHolder(getLayoutInflater().inflate(R.layout.item_extendedelem, viewGroup, false));
        }

        public void onBindViewHolder(@NotNull RecyclerView.ViewHolder viewHolder, int i) {
            if (i == HEADER) {
                setupHeader((BigPicViewHolder) viewHolder);
            } else {
                setupItem((EditTextViewHolder) viewHolder, i);
            }
        }

        private int getColorFromAttrs(Context context, int resId, int defaultColor) {
            TypedValue a = new TypedValue();
            context.getTheme().resolveAttribute(resId, a, true);
            if (a.type >= TypedValue.TYPE_FIRST_COLOR_INT && a.type <= TypedValue.TYPE_LAST_COLOR_INT) {
                return a.data;
            } else {
                return defaultColor;
            }
        }

        private int getColorPrimary() {
            return getColorFromAttrs(requireActivity(), R.attr.colorOnSurface, Color.parseColor("#000000"));
        }

        public void setupItem(EditTextViewHolder editTextViewHolder, int i) {
            editTextViewHolder.editText.setHint(null);
            final ExtendedElement extendedElement = extendedElements.get(i);
            TextWatcher r1 = new TextWatcher() {
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void afterTextChanged(Editable editable) {
                    extendedElementsDao.updateText(editable.toString(), extendedElement.id);
                    extendedElement.text = editable.toString();
                }
            };
            editTextViewHolder.editText.removeTextChangedListener(r1);
            editTextViewHolder.editText.setText(extendedElement.text);
            editTextViewHolder.editText.setImeOptions(0);
            editTextViewHolder.editText.setTextColor(extendedElement.reserved ? Color.parseColor("#DE0D69") : getColorPrimary());

            if (i == getItemCount() - 1) {
                editTextViewHolder.editText.requestFocus();
            }
            editTextViewHolder.editText.addTextChangedListener(r1);
        }

        public void setupHeader(BigPicViewHolder bigPicViewHolder) {
            final MenuElementsDao menuElementsDao = App.getInstance().getDatabase().menuElementsDao();
            bigPhoto = bigPicViewHolder.imageView;
            description = bigPicViewHolder.descriptionEditText;
            try {
                Glide.with(ElemInfoFragment.this).load(new File(element.imageSource)).apply(new RequestOptions().centerCrop()).into(bigPhoto);
            } catch (Exception unused) {
                bigPhoto.setVisibility(View.GONE);
            }
            bigPicViewHolder.descriptionEditText.setText(element.description);
            bigPicViewHolder.descriptionEditText.addTextChangedListener(new TextWatcher() {
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void afterTextChanged(Editable editable) {
                    menuElementsDao.updateDescription(editable.toString(), elemId);
                }
            });
            SharedPreferences defaultPreferences = App.getDefaultPreferences();
            if (!defaultPreferences.getBoolean(elemId + "_desc", true)) {
                bigPicViewHolder.descriptionEditText.setVisibility(View.GONE);
            }
        }

        public int getItemCount() {
            return extendedElements.size();
        }

        public class BigPicViewHolder extends RecyclerView.ViewHolder {
            public EditText descriptionEditText;
            public ImageView imageView;

            public BigPicViewHolder(View view) {
                super(view);
                this.imageView = view.findViewById(R.id.image);
                this.descriptionEditText = view.findViewById(R.id.edittext);
            }
        }

        public class EditTextViewHolder extends RecyclerView.ViewHolder {
            public EditText editText;

            public EditTextViewHolder(View view) {
                super(view);
                this.editText = view.findViewById(R.id.edittext);
            }
        }
    }
}
