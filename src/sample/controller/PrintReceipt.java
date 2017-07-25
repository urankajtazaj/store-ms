package sample.controller;

import sample.Enums.ButtonType;
import sample.Enums.NotificationType;

import javax.print.*;
import javax.print.event.PrintJobAdapter;
import javax.print.event.PrintJobEvent;

public class PrintReceipt {
    public PrintReceipt(String text) {

        DocFlavor flavor = DocFlavor.STRING.TEXT_PLAIN;

        PrintService service = PrintServiceLookup.lookupDefaultPrintService();

        if (service != null) {
            DocPrintJob printJob = service.createPrintJob();

            printJob.addPrintJobListener(new JobMonitor());

            Doc doc = new SimpleDoc(text, flavor, null);

            try {
                printJob.print(doc, null);
            } catch (PrintException e) {
                e.printStackTrace();
            }
        }
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
