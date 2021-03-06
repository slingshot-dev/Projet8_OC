package tourGuide.Modeles;

import Modeles.Location;

import java.util.List;
import java.util.UUID;

/**
 * Modele de donnéew pour la Localisation de k'utilisateur
 */

public class UserLocation {

    private UUID userUuid;
    private Location userLoc;

    public UserLocation(UUID userUuid, Location userLoc) {
        this.userUuid = userUuid;
        this.userLoc = userLoc;
    }

    public UUID getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(UUID userUuid) {
        this.userUuid = userUuid;
    }

    public Location getUserLoc() {
        return userLoc;
    }

    public void setUserLoc(Location userLoc) {
        this.userLoc = userLoc;
    }
}
