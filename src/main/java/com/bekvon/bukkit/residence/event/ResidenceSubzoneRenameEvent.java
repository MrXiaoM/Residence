package com.bekvon.bukkit.residence.event;

import com.bekvon.bukkit.residence.protection.ClaimedResidence;

public class ResidenceSubzoneRenameEvent extends CancellableResidenceEvent {
    protected final String NewSubName;
    protected final String OldSubName;
    protected final ClaimedResidence res;

    public ResidenceSubzoneRenameEvent(ClaimedResidence resref, String NewName, String OldName) {
        super("RESIDENCE_SUBZONE_RENAME", resref);
        NewSubName = NewName;
        OldSubName = OldName;
        res = resref;
    }

    public String getNewSubResidenceName() {
        return NewSubName;
    }

    public String getOldSubResidenceName() {
        return OldSubName;
    }

    @Override
    public ClaimedResidence getResidence() {
        return res;
    }
}
