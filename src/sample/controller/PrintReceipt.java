package sample.controller;

import sample.Enums.ButtonType;
import sample.Enums.NotificationType;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.event.PrintJobAdapter;
import javax.print.event.PrintJobEvent;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class PrintReceipt {
    public PrintReceipt(String text) {

        PrintService service = PrintServiceLookup.lookupDefaultPrintService();

        InputStream is = new ByteArrayInputStream((text + "\f").getBytes());

        System.out.println(text);

        PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
        pras.add(new Copies(1));

        DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;

        if (service != null) {
            DocPrintJob printJob = service.createPrintJob();

            printJob.addPrintJobListener(new JobMonitor());

            Doc doc = new SimpleDoc(is, flavor, null);

            try {
                printJob.print(doc, pras);
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean availablePrinter() {
        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
        if (services.length > 0) return true;
        return false;
    }

    public static class JobMonitor extends PrintJobAdapter {
        @Override
        public void printJobCompleted(PrintJobEvent pje) {
            Notification ntf = new Notification("Faktura u shtyp me sukses");
            ntf.setType(NotificationType.SUCCESS);
            ntf.setButton(ButtonType.NO_BUTTON);
        }

        @Override
        public void printJobFailed(PrintJobEvent pje) {
            Notification ntf = new Notification("Faktura nuk mund te shtypet");
            ntf.setType(NotificationType.ERROR);
            ntf.setButton(ButtonType.NO_BUTTON);
        }
    }

}
