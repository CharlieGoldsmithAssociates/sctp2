/*
 * BSD 3-Clause License
 *
 * Copyright (c) 2022, CGATechnologies
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.cga.sctp.beneficiaries;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code39Writer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.cos.COSObjectKey;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.cga.sctp.core.BaseComponent;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PassbookGenerator extends BaseComponent {

    public void replace(String fileName) {
        try (PDDocument document = PDDocument.load(new File(fileName))) {
            document.getDocument().getObjectByType(COSName.IMAGE);
            for (Map.Entry<COSObjectKey, Long> cosObjectKeyLongEntry : document.getDocument().getXrefTable().entrySet()) {
                COSObject object = document.getDocument().getObjectFromPool(
                        cosObjectKeyLongEntry.getKey());
                LOG.info("name: {}, type: {}", object.getDictionaryObject(COSName.NAME),
                        object.getDictionaryObject(COSName.SUBTYPE));
                if (object.getDictionaryObject(COSName.SUBTYPE) == COSName.IMAGE) {
                    //changeImage(object, doc);
                }
            }
        } catch (Exception e) {
            LOG.error("", e);
        }
    }

    public boolean generate(String fileName) {
        try (PDDocument document = new PDDocument()) {
            PDPage page;

            document.addPage(page = new PDPage(PDRectangle.A4));

            addBarcode("1234", document, page);

            /*try (PDPageContentStream cont = new PDPageContentStream(document, page)) {

                cont.beginText();

                cont.setFont(PDType1Font.TIMES_ROMAN, 12);
                cont.setLeading(14.5f);

                cont.newLineAtOffset(25, 700);
                String line1 = "World War II (often abbreviated to WWII or WW2), "
                        + "also known as the Second World War,";
                cont.showText(line1);

                cont.newLine();

                String line2 = "was a global war that lasted from 1939 to 1945, "
                        + "although related conflicts began earlier.";
                cont.showText(line2);
                cont.newLine();

                String line3 = "It involved the vast majority of the world's "
                        + "countries—including all of the great powers—";
                cont.showText(line3);
                cont.newLine();

                String line4 = "eventually forming two opposing military "
                        + "alliances: the Allies and the Axis.";
                cont.showText(line4);
                cont.newLine();

                cont.endText();
            }*/
            document.save(fileName);
            return true;
        } catch (Exception e) {
            LOG.error("Error generating pdf", e);
        }
        return false;
    }

    private void addBarcode(String text, PDDocument document, PDPage page) throws IOException {
        try (PDPageContentStream stream = new PDPageContentStream(document, page)) {
            BitMatrix bitMatrix;
            Code39Writer writer;
            Graphics2D graphics2D;

            Map<EncodeHintType, Object> hintMap = new HashMap<>();
            hintMap.put(EncodeHintType.MARGIN, 0);
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

            writer = new Code39Writer();
            bitMatrix = writer.encode(text, BarcodeFormat.CODE_39, 200, 200, hintMap);

            //BufferedImage buffImg = MatrixToImageWriter.toBufferedImage(bitMatrix);

            PDImageXObject image = new PDImageXObject(document);

            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", image.getStream().createOutputStream());

            stream.drawImage(image, 5, 8);
        }
    }
}
