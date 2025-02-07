package in.virit.ff.ferrymap.finnishterrainmap;

public class AbstractMaastokuvionReuna extends BasicLine {
    public AbstractMaastokuvionReuna(String hexColor) {
        super("maastokuvionreuna", hexColor);
        setMinZoom(12);
    }
}
