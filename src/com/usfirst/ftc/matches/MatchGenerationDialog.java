//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.usfirst.ftc.matches;

import com.FIRST.FTC.Common.Enumerations.MatchType;
import com.usfirst.ftc.ErrorDialog;
import com.usfirst.ftc.StringBundle;
import com.usfirst.ftc.event.DivisionController;
import com.usfirst.ftc.event.EventType;
import com.usfirst.ftc.teams.Team;
import com.usfirst.ftc.teams.TeamController;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class MatchGenerationDialog extends JDialog {
    public static void main(String[] args) {
        int[] teamNums = {3507,7738,8817,10091,10138,11848,10635,5037};
        ArrayList<Team> teams = new ArrayList<>();
        for (int teamNum : teamNums) {
            Team team = new Team();
            team.setNumber(teamNum);
            teams.add(team);
        }
        Vector<Team> teamVector = new Vector<>(teams);
        TeamController.getInstance().addTeams(teamVector);

        MatchGenerationDialog dialog = new MatchGenerationDialog(null, false);
        dialog.setVisible(true);
    }

    static final long serialVersionUID = 1L;
    private JPanel buttonPanel;
    private JButton cancelButton;
    private JButton deleteButton;
    private JPanel mainPanel;
    private JButton okButton;
    private JLabel matchLimitLabel;
    private JSpinner matchLimitSpinner;
    private JComboBox typeComboBox;
    private JLabel typeLabel;

    public MatchGenerationDialog(Frame parent, boolean modal) {
        super(parent, modal);
        this.initComponents();
    }

    public final void generateAction() {
        RoundRobinGenerator generator = new RoundRobinGenerator();
        int maxMatchCount = (Integer) this.matchLimitSpinner.getValue();
        List<Team> teamList = TeamController.getInstance().getTeams().stream().filter(Team::isParticipating).collect(Collectors.toList());
        List<Match> matchList = generator.generateRoundRobinSchedule(teamList, maxMatchCount);

        int perTeam = (matchList.size() * 2) / teamList.size();
        String typeStr = (String)this.typeComboBox.getSelectedItem();
        MatchType type = MatchType.QUALIFICATION;
        if(typeStr.compareTo(StringBundle.getString("MatchType.Practice")) == 0) {
            type = MatchType.PRACTICE;
        }

        MatchType finalType = type;
        MatchController.getInstance().clearMatches(type);
        matchList.forEach((m) -> m.setMatchType(finalType));
        matchList.forEach((m) -> MatchController.getInstance().setMatch(m));
        DivisionController.getInstance().getDivision().setMatchesPerTeam(perTeam);
        DivisionController.getInstance().getDivision().setMatchesGenerated(true);
        MatchGenerationDialog.this.setVisible(false);

    }

    private void initComponents() {
        this.mainPanel = new JPanel();
        this.matchLimitLabel = new JLabel();
        this.matchLimitSpinner = new JSpinner();
        this.typeLabel = new JLabel();
        this.typeComboBox = new JComboBox();
        this.buttonPanel = new JPanel();
        this.okButton = new JButton();
        this.cancelButton = new JButton();
        this.deleteButton = new JButton();
        this.setDefaultCloseOperation(2);
        this.setTitle(StringBundle.getString("MatchGenerationDialog.title"));
        this.setCursor(new Cursor(0));
        this.setModal(true);
        this.setName("Form");
        this.setResizable(false);
        this.getContentPane().setLayout(new GridBagLayout());
        this.mainPanel.setName("mainPanel");
        this.mainPanel.setLayout(new GridBagLayout());
        this.matchLimitLabel.setText("Maximum number of matches");
        this.matchLimitLabel.setName("matchLimitLabel");
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 1;
        gridBagConstraints.anchor = 18;
        this.mainPanel.add(this.matchLimitLabel, gridBagConstraints);
        EventType eType = DivisionController.getInstance().getType();
        int teamCount = TeamController.getInstance().getTeams().size();
        int minMatches = 0;
        int maxMatches = (teamCount * (teamCount - 1)) / 2;
        this.matchLimitSpinner.setModel(new SpinnerNumberModel(maxMatches, minMatches, maxMatches, 1));
        this.matchLimitSpinner.setName("matchLimitSpinner");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = 18;
        this.mainPanel.add(this.matchLimitSpinner, gridBagConstraints);
        this.typeLabel.setText(StringBundle.getString("MatchGenerationDialog.type"));
        this.typeLabel.setName("typeLabel");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = 1;
        gridBagConstraints.anchor = 18;
        this.mainPanel.add(this.typeLabel, gridBagConstraints);
        this.typeComboBox.setModel(new DefaultComboBoxModel(new String[]{StringBundle.getString("MatchType.Qualification"), StringBundle.getString("MatchType.Practice")}));
        this.typeComboBox.setName("typeComboBox");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = 18;
        this.mainPanel.add(this.typeComboBox, gridBagConstraints);
        this.buttonPanel.setName("buttonPanel");
        this.buttonPanel.setLayout(new GridBagLayout());
        this.okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                MatchGenerationDialog.this.generateAction();
            }
        });
        this.okButton.setText(StringBundle.getString("MatchGenerationDialog.generate"));
        this.okButton.setName("okButton");
        this.okButton.setPreferredSize(new Dimension(100, 29));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = 1;
        gridBagConstraints.anchor = 17;
        this.buttonPanel.add(this.okButton, gridBagConstraints);
        this.cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MatchGenerationDialog.this.setVisible(false);
            }
        });
        this.cancelButton.setText(StringBundle.getString("general.Cancel"));
        this.cancelButton.setName("cancelButton");
        this.cancelButton.setPreferredSize(new Dimension(100, 29));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = 1;
        this.buttonPanel.add(this.cancelButton, gridBagConstraints);
        this.deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String typeStr = (String)MatchGenerationDialog.this.typeComboBox.getSelectedItem();
                MatchType type = MatchType.QUALIFICATION;
                if(typeStr.compareTo("MatchType.Practice") == 0) {
                    type = MatchType.PRACTICE;
                }

                MatchController.getInstance().clearMatches(type);
                MatchGenerationDialog.this.setVisible(false);
            }
        });
        this.deleteButton.setText(StringBundle.getString("general.Delete"));
        this.deleteButton.setName("deleteButton");
        this.deleteButton.setPreferredSize(new Dimension(100, 29));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = 1;
        this.buttonPanel.add(this.deleteButton, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 0;
        gridBagConstraints.fill = 2;
        gridBagConstraints.insets = new Insets(5, 0, 0, 0);
        this.mainPanel.add(this.buttonPanel, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(20, 20, 20, 20);
        this.getContentPane().add(this.mainPanel, gridBagConstraints);
        this.pack();
    }
}
