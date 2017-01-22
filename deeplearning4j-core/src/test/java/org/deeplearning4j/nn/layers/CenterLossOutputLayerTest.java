/*
 *
 *  * Copyright 2015 Skymind,Inc.
 *  *
 *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *    you may not use this file except in compliance with the License.
 *  *    You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *    Unless required by applicable law or agreed to in writing, software
 *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *    See the License for the specific language governing permissions and
 *  *    limitations under the License.
 *
 */

package org.deeplearning4j.nn.layers;

import org.deeplearning4j.datasets.iterator.impl.IrisDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.gradientcheck.GradientCheckUtil;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.distribution.NormalDistribution;
import org.deeplearning4j.nn.conf.layers.CenterLossOutputLayer;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.GravesLSTM;
import org.deeplearning4j.nn.conf.preprocessor.FeedForwardToRnnPreProcessor;
import org.deeplearning4j.nn.conf.preprocessor.RnnToFeedForwardPreProcessor;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.layers.recurrent.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.api.IterationListener;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.junit.Before;
import org.junit.Test;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.buffer.DataBuffer;
import org.nd4j.linalg.api.buffer.util.DataTypeUtil;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.SplitTestAndTrain;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * Test CenterLossOutputLayer.
 *
 * @author Justin Long (@crockpotveggies)
 */
public class CenterLossOutputLayerTest {
    private static final Logger log = LoggerFactory.getLogger(CenterLossOutputLayerTest.class);

		private ComputationGraph getGraph(int numLabels, double lambda) {
				Nd4j.getRandom().setSeed(12345);
				ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder()
						.seed(12345)
						.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
						.weightInit(WeightInit.DISTRIBUTION).dist(new NormalDistribution(0, 1))
						.updater(Updater.NONE).learningRate(1.0)
						.graphBuilder()
						.addInputs("input1")
						.addLayer("l1", new DenseLayer.Builder().nIn(4).nOut(5).activation(Activation.RELU).build(), "input1")
						.addLayer("lossLayer", new CenterLossOutputLayer.Builder()
								.lossFunction(LossFunctions.LossFunction.MCXENT)
								.nIn(5).nOut(numLabels).lambda(lambda)
								.activation(Activation.SOFTMAX).build(), "l1")
						.setOutputs("lossLayer")
						.pretrain(false).backprop(true)
						.build();

				ComputationGraph graph = new ComputationGraph(conf);
				graph.init();

				return graph;
		}

    @Test
    public void testLambdaConf() {
			double[] lambdas = new double[]{0.1, 0.01};
			double[] results = new double[2];
			int numClasses = 2;

			INDArray input = Nd4j.rand(150,4);
			INDArray labels = Nd4j.zeros(150,numClasses);
			Random r = new Random(12345);
			for( int i=0; i<150; i++ ){
				labels.putScalar(i,r.nextInt(numClasses),1.0);
			}
			ComputationGraph graph;

			for(int i = 0; i < lambdas.length; i++) {
				graph = getGraph(numClasses, lambdas[i]);
				graph.setInput(0, input);
				graph.setLabel(0,labels);
				graph.computeGradientAndScore();
				results[i] = graph.score();
			}

			assertNotEquals(results[0], results[1]);
		}
}
