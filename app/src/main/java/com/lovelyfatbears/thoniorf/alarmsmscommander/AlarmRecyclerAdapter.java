package com.lovelyfatbears.thoniorf.alarmsmscommander;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by thoniorf on 7/28/17.
 */

public class AlarmRecyclerAdapter extends RecyclerView.Adapter<AlarmRecyclerAdapter.AlarmViewHolder> {


    public static class AlarmViewHolder extends RecyclerView.ViewHolder {

        Alarm alarm;
        MainActivity activity;
        CardView card;
        TextView alarmName;
        ImageButton btn_more, btn_arm, btn_disarm, btn_status, btn_call, btn_delete;
        LinearLayout lay_action;
        boolean card_action_visibility;

        // aggiungere listeners

        public AlarmViewHolder(View itemView) {
            super(itemView);
            activity = (MainActivity) itemView.getContext();
            card_action_visibility = false;
            initWidget();
            initListeners();

        }

        public void initWidget() {
            card = (CardView) itemView.findViewById(R.id.cardview);
            alarmName = (TextView) itemView.findViewById(R.id.txt_alarm_name);
            btn_more = (ImageButton) itemView.findViewById(R.id.btn_more);
            lay_action = (LinearLayout) itemView.findViewById(R.id.lay_action);

            btn_arm = (ImageButton) itemView.findViewById(R.id.btn_arm);
            btn_disarm = (ImageButton) itemView.findViewById(R.id.btn_disarm);
            btn_status = (ImageButton) itemView.findViewById(R.id.btn_status);
            btn_call = (ImageButton) itemView.findViewById(R.id.btn_call);
            btn_delete = (ImageButton) itemView.findViewById(R.id.btn_delete);

        }

        public void initListeners() {
            btn_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    rotateAnimation((!card_action_visibility)?90:0);
                }
            });
            btn_arm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.trySend(alarm.number,alarm.password,alarm.codes[0]);
                }
            });
            btn_disarm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.trySend(alarm.number,alarm.password,alarm.codes[1]);
                }
            });
            btn_status.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.trySend(alarm.number,alarm.password,alarm.codes[2]);
                }
            });
            btn_call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.tryCall(alarm.number);
                }
            });
            btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.alarms.remove(getAdapterPosition());
                    rotateAnimation((!card_action_visibility)?90:0);
                    activity.recyAdapter.notifyDataSetChanged();
                }
            });
        }

        public void rotateAnimation(float value) {
            btn_more.animate()
                    .rotation(value)
                    .setDuration(itemView.getContext().getResources().getInteger(
                    android.R.integer.config_shortAnimTime))
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            fadeCardActions(card_action_visibility);
                        }
                    });
        }
        public void fadeCardActions(boolean visibility) {
            if(visibility) {
                lay_action.animate()
                        .alpha(0f)
                        .setDuration(itemView.getContext().getResources().getInteger(
                                android.R.integer.config_shortAnimTime))
                        .setListener(new AnimatorListenerAdapter() {

                            @Override
                            public void onAnimationEnd(Animator animator) {
                                lay_action.setVisibility(View.GONE);
                            }

                        });
                alarmName.setAlpha(0f);
                alarmName.setVisibility(View.VISIBLE);
                alarmName.animate()
                        .alpha(1f)
                        .setDuration(itemView.getContext().getResources().getInteger(
                                android.R.integer.config_shortAnimTime))
                        .setListener(null);
            } else {
                alarmName.animate()
                        .alpha(0f)
                        .setDuration(itemView.getContext().getResources().getInteger(
                                android.R.integer.config_shortAnimTime))
                        .setListener(new AnimatorListenerAdapter() {

                            @Override
                            public void onAnimationEnd(Animator animator) {
                                alarmName.setVisibility(View.GONE);
                            }

                        });
                lay_action.setAlpha(0f);
                lay_action.setVisibility(View.VISIBLE);
                lay_action.animate()
                        .alpha(1f)
                        .setDuration(itemView.getContext().getResources().getInteger(
                                android.R.integer.config_shortAnimTime))
                        .setListener(null);
            }
            card_action_visibility = !card_action_visibility;
        }
    }

    List<Alarm> alarms;

    public AlarmRecyclerAdapter(List<Alarm> alarms) {
        this.alarms = alarms;
    }

    @Override
    public int getItemCount() {
        return alarms.size();
    }

    @Override
    public AlarmViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_list_row, viewGroup, false);
        return new AlarmViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AlarmViewHolder holder, int position) {
        holder.alarm = new Alarm(alarms.get(position).number, alarms.get(position).password, alarms.get(position).name);
        holder.alarmName.setText(holder.alarm.name);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
