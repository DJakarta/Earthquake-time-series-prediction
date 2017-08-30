require("forecast");

inputData = unlist(scan(file = "simpleAutomaticPrediction.in", what = double()));
predictionLength = inputData[1];
inputSeries = inputData[2 : length(inputData)];

model = auto.arima(ts(inputSeries));

pred = forecast(model, h = predictionLength);

outputSeries = as.numeric(pred$mean);

componentRes = residuals(model);

result = c(length(outputSeries), outputSeries, length(componentRes), componentRes);
write(result, file = "simpleAutomaticPrediction.out", ncolumns = 1);