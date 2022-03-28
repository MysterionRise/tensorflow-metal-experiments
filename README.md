# Repo dedicated to various Tensorflow-metal experiments

## How to run on M1 chip with GPU support

Install conda:
```
chmod +x ~/Downloads/Miniforge3-MacOSX-arm64.sh
sh ~/Downloads/Miniforge3-MacOSX-arm64.sh
```

Create conda environment (not for Windows):
```
source ~/miniforge3/bin/activate
conda create -n tensorflow python=3.9.5
conda activate tensorflow
```

Install dependencies (for M1 chips)
```
conda install -c apple tensorflow-deps
python -m pip install tensorflow-macos
python -m pip install tensorflow-metal
brew install libjpeg
conda install -y matplotlib jupyterlab
```

Install dependencies (for x64 MacBooks)
```
conda install tensorflow
brew install libjpeg
conda install -y matplotlib jupyterlab
```

Install dependencies & create env (for Win GPU machine) 
```
conda create -n tf-gpu tensorflow-gpu
conda activate tf-gpu
conda install -y matplotlib jupyterlab
```

Test installation by running Jupyter lab
```
jupyter lab
```
Run `tensorflow_test.ipynb`, check Activity monitor to check that GPU is utilised

Test results:

```
+-----------------------------------------------------------------------------+-----+-----------------------+
|                                  Hardware                                   | GPU | Average training time |
+-----------------------------------------------------------------------------+-----+-----------------------+
| Apple M1 Max with 10-core CPU, 32-core GPU, 16-core Neural Engine 64 Gb RAM | Y   | 106 sec               |
| 2.4 Ghz 8-core i9 32 Gb                                                     | N   | 254 sec               |
| Nvidia 8Gb GDDR6 RTX2070 Windows 10 Anaconda                                | N   | 55 sec                |
+-----------------------------------------------------------------------------+-----+-----------------------+
```
