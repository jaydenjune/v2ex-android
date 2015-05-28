package com.czbix.v2ex.ui.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.czbix.v2ex.R;
import com.czbix.v2ex.model.Comment;
import com.czbix.v2ex.network.ImageLoader;
import com.google.common.base.Preconditions;

import java.util.List;

public class CommentAdapter extends BaseAdapter {
    private final LayoutInflater mInflater;
    private final View mHeaderView;
    private List<Comment> mCommentList;

    public CommentAdapter(Context context, View headerView) {
        mInflater = LayoutInflater.from(context);
        mHeaderView = headerView;
    }

    public void setDataSource(List<Comment> comments) {
        mCommentList = comments;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mCommentList == null) {
            return 1;
        }

        return mCommentList.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        if (position == 0) {
            return mHeaderView;
        }

        return position - 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return IGNORE_ITEM_VIEW_TYPE;
        }
        return super.getItemViewType(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (position == 0) {
            return mHeaderView;
        }
        position--;

        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.view_comment, parent , false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = ((ViewHolder) convertView.getTag());
            Preconditions.checkNotNull(viewHolder);
        }

        final Comment comment = mCommentList.get(position);
        viewHolder.fillData(comment);

        return convertView;
    }

    private static class ViewHolder implements ImageLoader.Callback {
        private final TextView mContent;
        private final ImageView mAvatar;
        private final TextView mUsername;
        private final TextView mReplyTime;

        private volatile int mId;

        public ViewHolder(View view) {
            mAvatar = ((ImageView) view.findViewById(R.id.avatar_img));
            mContent = (TextView) view.findViewById(R.id.content);
            mUsername = (TextView) view.findViewById(R.id.username_tv);
            mReplyTime = ((TextView) view.findViewById(R.id.time_tv));
        }

        public void fillData(Comment comment) {
            if (mId == comment.getId()) {
                return;
            }

            mId = comment.getId();

            mContent.setText(Html.fromHtml(comment.getContent()));
            mUsername.setText(comment.getMember().getUsername());
            mReplyTime.setText(comment.getReplyTime());

            setAvatarImg(comment);
        }

        public void setAvatarImg(Comment comment) {
            final String url = comment.getMember().getAvatar().getUrlByDp(32);
            mAvatar.setImageResource(R.drawable.avatar_default);
            ImageLoader.getInstance().add(mId, mAvatar, url, this);
        }

        @Override
        public boolean isTaskIdValid(int taskId) {
            return mId == taskId;
        }
    }
}
