package com.example.incoiceextraction.utils;

import android.graphics.Bitmap;
import android.widget.TextView;
import android.util.Log;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

public class OCRProcessor {

    public void processImage(Bitmap bitmap, TextView outputView) {

        InputImage image = InputImage.fromBitmap(bitmap, 0);

        com.google.mlkit.vision.text.TextRecognizer recognizer =
                TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        recognizer.process(image)
                .addOnSuccessListener(visionText -> {

                    String extractedText = visionText.getText();

                    // 🔍 DEBUG: print OCR text
                    Log.d("OCR_DEBUG", "OCR TEXT: " + extractedText);

                    FieldExtractor extractor = new FieldExtractor();

                    String result = extractor.extractFields(extractedText);

                    outputView.setText(result);

                })
                .addOnFailureListener(e -> {

                    outputView.setText("OCR Failed");

                });
    }
}