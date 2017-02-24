package megabasterd;

import java.awt.Dialog;
import java.awt.Font;
import java.awt.event.WindowEvent;
import static java.awt.event.WindowEvent.WINDOW_CLOSING;
import java.io.IOException;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.logging.Logger.getLogger;
import javax.swing.JOptionPane;
import static megabasterd.MainPanel.FONT_DEFAULT;
import static megabasterd.MainPanel.THREAD_POOL;
import static megabasterd.MiscTools.deflateURL;
import static megabasterd.MiscTools.extractFirstMegaLinkFromString;
import static megabasterd.MiscTools.extractStringFromClipboardContents;
import static megabasterd.MiscTools.findFirstRegex;
import static megabasterd.MiscTools.swingReflectionInvoke;
import static megabasterd.MiscTools.swingReflectionInvokeAndWaitForReturn;
import static megabasterd.MiscTools.updateFont;

/**
 *
 * @author tonikelope
 */
public final class StreamerDialog extends javax.swing.JDialog implements ClipboardChangeObserver {

    private final ClipboardSpy _clipboardspy;

    /**
     * Creates new form Streamer
     *
     * @param clipboardspy
     */
    public StreamerDialog(java.awt.Frame parent, boolean modal, ClipboardSpy clipboardspy) {
        super(parent, modal);
        initComponents();

        _clipboardspy = clipboardspy;

        MiscTools.swingInvokeIt(new Runnable() {

            @Override
            public void run() {
                updateFont(put_label, FONT_DEFAULT, Font.PLAIN);
                updateFont(original_link_textfield, FONT_DEFAULT, Font.PLAIN);
                updateFont(dance_button, FONT_DEFAULT, Font.PLAIN);
            }
        }, true);

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        put_label = new javax.swing.JLabel();
        dance_button = new javax.swing.JButton();
        original_link_textfield = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Streamer");
        setResizable(false);

        put_label.setFont(new java.awt.Font("Dialog", 1, 20)); // NOI18N
        put_label.setText("Put your MEGA/MegaCrypter/ELC link here in order to get a streaming link:");
        put_label.setDoubleBuffered(true);

        dance_button.setBackground(new java.awt.Color(102, 204, 255));
        dance_button.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        dance_button.setForeground(new java.awt.Color(255, 255, 255));
        dance_button.setText("Let's dance, baby");
        dance_button.setDoubleBuffered(true);
        dance_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dance_buttonActionPerformed(evt);
            }
        });

        original_link_textfield.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        original_link_textfield.setDoubleBuffered(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(dance_button, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(put_label)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(original_link_textfield))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(put_label)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(original_link_textfield, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(dance_button, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        original_link_textfield.addMouseListener(new ContextMenuMouseListener());

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void dance_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dance_buttonActionPerformed

        dance_button.setEnabled(false);

        original_link_textfield.setEnabled(false);

        final Dialog tthis = this;

        THREAD_POOL.execute(new Runnable() {
            @Override
            public void run() {

                boolean error = false;

                String stream_link = null;

                String link = ((String) swingReflectionInvokeAndWaitForReturn("getText", original_link_textfield)).trim();

                if (link.length() > 0) {

                    try {

                        if (MiscTools.findFirstRegex("://enc", link, 0) != null) {

                            link = CryptTools.decryptMegaDownloaderLink(link);

                        } else if (MiscTools.findFirstRegex("://elc", link, 0) != null) {

                            HashSet links = CryptTools.decryptELC(link, ((MainPanelView) tthis.getParent()).getMain_panel());

                            if (links != null) {

                                link = (String) links.iterator().next();
                            }
                        }

                    } catch (Exception ex) {

                        error = true;

                        getLogger(StreamerDialog.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    String data;

                    if (findFirstRegex("://mega(\\.co)?\\.nz/#[^fF]", link, 0) != null || findFirstRegex("https?://[^/]+/![^!]+![0-9a-fA-F]+", link, 0) != null) {

                        stream_link = "http://localhost:1337/video/" + MiscTools.Bin2UrlBASE64(link.getBytes());

                    } else {

                        error = true;
                    }

                } else {

                    error = true;
                }

                if (error) {

                    JOptionPane.showMessageDialog(tthis, "Please, paste a Mega/MegaCrypter/ELC link!", "Error", JOptionPane.ERROR_MESSAGE);

                    swingReflectionInvoke("setText", original_link_textfield, "");

                    swingReflectionInvoke("setEnabled", dance_button, true);

                    swingReflectionInvoke("setEnabled", original_link_textfield, true);

                } else {

                    try {

                        MiscTools.copyTextToClipboard(deflateURL(stream_link));

                        JOptionPane.showMessageDialog(tthis, "Streaming link was copied to clipboard!\n(Remember to keep MegaBasterd running in background while playing)");

                        dispose();

                        getParent().dispatchEvent(new WindowEvent(tthis, WINDOW_CLOSING));

                    } catch (IOException ex) {
                        Logger.getLogger(StreamerDialog.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            }
        });
    }//GEN-LAST:event_dance_buttonActionPerformed

    @Override
    public void notifyClipboardChange() {

        String link = extractFirstMegaLinkFromString(extractStringFromClipboardContents(_clipboardspy.getContents()));

        if (!link.contains("/#F!")) {

            swingReflectionInvoke("setText", original_link_textfield, link);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton dance_button;
    private javax.swing.JTextField original_link_textfield;
    private javax.swing.JLabel put_label;
    // End of variables declaration//GEN-END:variables

}
