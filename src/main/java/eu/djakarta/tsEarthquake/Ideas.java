package eu.djakarta.tsEarthquake;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.TimeUnit;

import javax.swing.ToolTipManager;

public class Ideas {
  public static class InstantPopupMouseAdapter extends MouseAdapter {
    final int defaultDismissTimeout = ToolTipManager.sharedInstance().getDismissDelay();
    final int dismissDelayMinutes = (int) TimeUnit.MINUTES.toMillis(10);

    public void mouseEntered(MouseEvent me) {
      ToolTipManager.sharedInstance().setDismissDelay(dismissDelayMinutes);
    }

    public void mouseExited(MouseEvent me) {
      ToolTipManager.sharedInstance().setDismissDelay(defaultDismissTimeout);
    }
  }

  public static double[][] sampleData() {
    double[][] data = new double[2][50];
    for (int i = 0; i < 50; i++) {
      if (i < 20) {
        data[0][i] = 2 * i;
      }
      else {
        data[0][i] = i;
      }
      data[1][i] = i * i;
    }
    return data;
  }
}
