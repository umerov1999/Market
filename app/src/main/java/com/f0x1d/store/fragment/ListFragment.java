package com.f0x1d.store.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.f0x1d.store.App;
import com.f0x1d.store.R;
import com.f0x1d.store.activity.MainActivity;
import com.f0x1d.store.db.Database;
import com.f0x1d.store.db.daos.MenuElementsDao;
import com.f0x1d.store.db.entities.ExtendedElement;
import com.f0x1d.store.db.entities.MenuElement;
import com.f0x1d.store.settings.NightMode;
import com.f0x1d.store.settings.Settings;
import com.f0x1d.store.view.CenteredToolbar;
import com.f0x1d.store.view.RoundedBackgroundSpan;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ListFragment extends Fragment {
    public ListItemsAdapter adapter;
    public ExtendedFloatingActionButton createButton;
    public FloatingActionButton NightModeBtn;
    public FloatingActionButton HomeBtn;
    public long inFolderId;
    public List<MenuElement> menuElements = new ArrayList<>();
    public MenuElementsDao menuElementsDao;
    public RecyclerView recyclerView;
    public CenteredToolbar toolbar;

    public static ListFragment newInstance(long inFolderId, String folder_name) {
        Bundle bundle = new Bundle();
        bundle.putLong("inFolderId", inFolderId);
        bundle.putString("folder_name", folder_name);
        ListFragment listFragment = new ListFragment();
        listFragment.setArguments(bundle);
        return listFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_list, container, false);

        this.inFolderId = requireArguments().getLong("inFolderId");
        this.HomeBtn = root.findViewById(R.id.home_button);
        this.HomeBtn.setVisibility(this.inFolderId == -1 ? View.GONE : View.VISIBLE);
        this.createButton = root.findViewById(R.id.btn_create);
        this.NightModeBtn = root.findViewById(R.id.day_night);
        this.recyclerView = root.findViewById(R.id.recycler_view);
        this.toolbar = root.findViewById(R.id.toolbar);
        this.toolbar.setTitle(requireArguments().getString("folder_name"));
        this.menuElementsDao = App.getInstance().getDatabase().menuElementsDao();
        this.menuElements = this.menuElementsDao.getAll(this.inFolderId);
        this.NightModeBtn.setOnClickListener(v -> {
            if (Settings.get().isDarkModeEnabled(requireActivity())) {
                Settings.get().switchNightMode(NightMode.DISABLE);
                AppCompatDelegate.setDefaultNightMode(NightMode.DISABLE);
            } else {
                Settings.get().switchNightMode(NightMode.ENABLE);
                AppCompatDelegate.setDefaultNightMode(NightMode.ENABLE);
            }
        });
        this.HomeBtn.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).clearBackStack();
            ((MainActivity) requireActivity()).replaceFragment(ListFragment.newInstance(-1, getString(R.string.root)), "main_list", false, null);
        });
        this.createButton.setOnClickListener(view1 -> {
            final View inflate = getLayoutInflater().inflate(R.layout.dialog_edit_text_with_type, null);
            ((TextInputLayout) inflate.findViewById(R.id.edittext_layout)).setHint(getString(R.string.choose_element_name));
            MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(requireActivity());
            materialAlertDialogBuilder.setTitle(R.string.choose_element_name);
            materialAlertDialogBuilder.setView(inflate);
            materialAlertDialogBuilder.setPositiveButton(R.string.add, (dialogInterface, i) -> {
                MenuElement menuElement = MenuElement.create_elem(Database.getMenuLastId() + 1, inFolderId, ((EditText) inflate.findViewById(R.id.edittext)).getText().toString(), null, ((MaterialCheckBox) inflate.findViewById(R.id.checkbox_folder)).isChecked(), "");
                menuElementsDao.insert(menuElement);
                ((MainActivity) requireActivity()).replaceFragment(ElemInfoFragment.newInstance(menuElement.id), "info", true, null);
            });
            materialAlertDialogBuilder.show();
        });
        this.recyclerView.setLayoutManager(new GridLayoutManager(requireActivity(), 2, GridLayoutManager.VERTICAL, false));
        RecyclerView recyclerView2 = this.recyclerView;
        ListItemsAdapter listItemsAdapter = new ListItemsAdapter();
        this.adapter = listItemsAdapter;
        recyclerView2.setAdapter(listItemsAdapter);
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
                return makeMovementFlags(0, 48);
            }

            public void onSwiped(@NotNull final RecyclerView.ViewHolder viewHolder, int i) {
                MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(requireActivity());
                materialAlertDialogBuilder.setCancelable(false);
                materialAlertDialogBuilder.setTitle(R.string.are_u_sure);
                materialAlertDialogBuilder.setMessage(R.string.what_would_u_like_do);
                materialAlertDialogBuilder.setPositiveButton(R.string.sell, (dialogInterface, i1) -> {
                    MenuElement remove = menuElements.get(viewHolder.getAdapterPosition());
                    if (remove.isFolder && !menuElementsDao.getAll(remove.id).isEmpty()) {
                        new MaterialAlertDialogBuilder(requireActivity()).setCancelable(false).setMessage(R.string.folder_non_empty)
                                .setPositiveButton(R.string.info_action, (dlo, i78) -> {
                                    dialogInterface.cancel();
                                    adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                                }).create().show();
                        return;
                    }

                    menuElements.remove(viewHolder.getAdapterPosition());

                    menuElementsDao.delete(remove.id);
                    menuElementsDao.deleteAllExtendElements(remove.id);
                    if (remove.imageSource != null && !remove.imageSource.isEmpty()) {
                        File vv = new File(remove.imageSource);
                        if (vv.exists()) {
                            vv.delete();
                        }
                    }
                    adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                });
                materialAlertDialogBuilder.setNeutralButton(R.string.cancel_action, (dialogInterface, i12) -> {
                    dialogInterface.cancel();
                    adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                });
                materialAlertDialogBuilder.show();
            }
        }).attachToRecyclerView(this.recyclerView);
        return root;
    }

    public void onStart() {
        super.onStart();
    }

    public class ListItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        public static final int ELEMENT = 0;
        public static final int FOLDER = 1;

        public ListItemsAdapter() {
            setHasStableIds(true);
        }

        @NotNull
        public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup viewGroup, int i) {
            if (i == ELEMENT) {
                return new ListItemElementViewHolder(getLayoutInflater().inflate(R.layout.item_element, viewGroup, false));
            }
            if (i != FOLDER) {
                return null;
            }
            return new ListItemFolderViewHolder(getLayoutInflater().inflate(R.layout.item_folder, viewGroup, false));
        }

        public void onBindViewHolder(@NotNull RecyclerView.ViewHolder viewHolder, int i) {
            int itemViewType = getItemViewType(i);
            if (itemViewType == ELEMENT) {
                setupElement((ListItemElementViewHolder) viewHolder, i);
            } else if (itemViewType == FOLDER) {
                setupFolder((ListItemFolderViewHolder) viewHolder, i);
            }
        }

        public void setupElement(ListItemElementViewHolder listItemElementViewHolder, final int i) {
            final MenuElement menuElement = menuElements.get(i);
            if (menuElement.imageSource != null) {
                //listItemElementViewHolder.imageView.setVisibility(View.VISIBLE);
                Glide.with(ListFragment.this).asBitmap().load(new File(menuElement.imageSource)).apply(new RequestOptions().centerCrop().dontAnimate()).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).into(listItemElementViewHolder.imageView);
            } else {
                //listItemElementViewHolder.imageView.setVisibility(View.INVISIBLE);
                listItemElementViewHolder.imageView.setImageResource(R.drawable.ic_market_outline_56);
            }
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            boolean isFirst = true;
            for (ExtendedElement extendedElement : App.getInstance().getDatabase().extendedElementsDao().getAll(menuElement.id)) {
                SpannableString spannableString = new SpannableString(extendedElement.text);
                spannableString.setSpan(new RoundedBackgroundSpan(extendedElement.reserved ? Color.parseColor("#DE0D69") : Color.parseColor("#aaaaaa"), View.MEASURED_STATE_MASK, 30.0f), 0, spannableString.length(), 33);
                if (isFirst) {
                    isFirst = false;
                } else {
                    spannableStringBuilder.append(" ");
                }
                spannableStringBuilder.append(spannableString);
            }
            if (spannableStringBuilder.length() != 0) {
                if (!menuElement.name.isEmpty()) {
                    spannableStringBuilder.insert(0, menuElement.name).insert(menuElement.name.length(), "\n\n");
                } else {
                    spannableStringBuilder.insert(0, "\n\n");
                }
            } else if (!menuElement.name.isEmpty()) {
                spannableStringBuilder.insert(0, menuElement.name);
            }
            listItemElementViewHolder.text.setText(spannableStringBuilder);
            listItemElementViewHolder.itemView.setOnClickListener(view -> {
                menuElementsDao.changeEditTime(menuElement.id, System.currentTimeMillis());
                ((MainActivity) requireActivity()).replaceFragment(ElemInfoFragment.newInstance(menuElement.id), "info", true, null);
            });
            listItemElementViewHolder.itemView.setOnLongClickListener(view -> {
                final View inflate = getLayoutInflater().inflate(R.layout.dialog_edit_text, null);
                ((TextInputLayout) inflate.findViewById(R.id.edittext_layout)).setHint(getString(R.string.choose_element_name_new));
                MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(requireActivity());
                materialAlertDialogBuilder.setTitle(R.string.choose_element_name_new);
                materialAlertDialogBuilder.setView(inflate);
                materialAlertDialogBuilder.setPositiveButton(R.string.ok_action, (dialogInterface, i1) -> {
                    menuElementsDao.updateName(((EditText) inflate.findViewById(R.id.edittext)).getText().toString(), menuElement.id);
                    menuElements.get(i).name = ((EditText) inflate.findViewById(R.id.edittext)).getText().toString();
                    adapter.notifyItemChanged(i);
                });
                materialAlertDialogBuilder.show();
                return false;
            });
        }

        public void setupFolder(ListItemFolderViewHolder listItemFolderViewHolder, int i) {
            MenuElement menuElement = menuElements.get(i);
            if (menuElement.imageSource != null) {
                //listItemFolderViewHolder.imageView.setVisibility(View.VISIBLE);
                Glide.with(ListFragment.this).asBitmap().load(new File(menuElement.imageSource)).apply(new RequestOptions().centerCrop().dontAnimate()).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).into(listItemFolderViewHolder.imageView);
            } else {
                //listItemFolderViewHolder.imageView.setVisibility(View.INVISIBLE);
                listItemFolderViewHolder.imageView.setImageResource(R.drawable.folder);
            }
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            /*
            boolean isFirst = true;
            for (ExtendedElement extendedElement : App.getInstance().getDatabase().extendedElementsDao().getAll(menuElement.id)) {
                SpannableString spannableString = new SpannableString(extendedElement.text);
                spannableString.setSpan(new RoundedBackgroundSpan(Color.parseColor("#aaaaaa"), View.MEASURED_STATE_MASK, 30.0f), 0, spannableString.length(), 33);
                if (isFirst) {
                    isFirst = false;
                } else {
                    spannableStringBuilder.append(" ");
                }
                spannableStringBuilder.append(spannableString);
            }
             */

            SpannableString spannableString = new SpannableString(String.valueOf(menuElementsDao.getAll(menuElement.id).size()));
            spannableString.setSpan(new RoundedBackgroundSpan(Color.parseColor("#aaaaaa"), View.MEASURED_STATE_MASK, 30.0f), 0, spannableString.length(), 33);
            spannableStringBuilder.append(spannableString);

            if (spannableStringBuilder.length() != 0) {
                if (!menuElement.name.isEmpty()) {
                    spannableStringBuilder.insert(0, menuElement.name).insert(menuElement.name.length(), "\n\n");
                }
            } else if (!menuElement.name.isEmpty()) {
                spannableStringBuilder.insert(0, menuElement.name);
            }
            listItemFolderViewHolder.text.setText(spannableStringBuilder);
            listItemFolderViewHolder.itemView.setOnLongClickListener(view -> {
                menuElementsDao.changeEditTime(menuElement.id, System.currentTimeMillis());
                ((MainActivity) requireActivity()).replaceFragment(ElemInfoFragment.newInstance(menuElement.id), "info", true, null);
                return true;
            });
            listItemFolderViewHolder.itemView.setOnClickListener(view -> {
                ((MainActivity) requireActivity()).replaceFragment(ListFragment.newInstance(menuElement.id, menuElement.name), "info", true, null);
            });
        }

        public int getItemCount() {
            return menuElements.size();
        }

        public long getItemId(int i) {
            return menuElements.get(i).id;
        }

        public int getItemViewType(int i) {
            return menuElements.get(i).isFolder ? FOLDER : ELEMENT;
        }

        public void onViewRecycled(@NotNull RecyclerView.ViewHolder viewHolder) {
            super.onViewRecycled(viewHolder);
            if (viewHolder instanceof ListItemElementViewHolder) {
                Glide.with(ListFragment.this).clear(((ListItemElementViewHolder) viewHolder).imageView);
            }
        }

        public class ListItemElementViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;
            public TextView text;

            public ListItemElementViewHolder(View view) {
                super(view);
                this.imageView = view.findViewById(R.id.image_view);
                this.text = view.findViewById(R.id.text);
            }
        }

        public class ListItemFolderViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;
            public TextView text;

            public ListItemFolderViewHolder(View view) {
                super(view);
                this.imageView = view.findViewById(R.id.image_view);
                this.text = view.findViewById(R.id.text);
            }
        }
    }
}
