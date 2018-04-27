namespace wpm2018a_v1
{
    partial class Form1
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.checkBox1 = new System.Windows.Forms.CheckBox();
            this.Payload_lbl = new System.Windows.Forms.Label();
            this.Topic_lbl = new System.Windows.Forms.Label();
            this.NID_lbl = new System.Windows.Forms.Label();
            this.PA_lbl = new System.Windows.Forms.Label();
            this.PA_data = new System.Windows.Forms.Label();
            this.NID_textbox = new System.Windows.Forms.TextBox();
            this.Connect_btn = new System.Windows.Forms.Button();
            this.PB_lbl = new System.Windows.Forms.Label();
            this.PC_lbl = new System.Windows.Forms.Label();
            this.PB_data = new System.Windows.Forms.Label();
            this.PC_data = new System.Windows.Forms.Label();
            this.PT_lbl = new System.Windows.Forms.Label();
            this.PT_data = new System.Windows.Forms.Label();
            this.Pavg_lbl = new System.Windows.Forms.Label();
            this.Pavg_min_lbl = new System.Windows.Forms.Label();
            this.EA_lbl = new System.Windows.Forms.Label();
            this.EB_lbl = new System.Windows.Forms.Label();
            this.EC_lbl = new System.Windows.Forms.Label();
            this.ET_lbl = new System.Windows.Forms.Label();
            this.PeakA_lbl = new System.Windows.Forms.Label();
            this.PeakB_lbl = new System.Windows.Forms.Label();
            this.PeakC_lbl = new System.Windows.Forms.Label();
            this.PeakT_lbl = new System.Windows.Forms.Label();
            this.Pavg_data = new System.Windows.Forms.Label();
            this.Pavg_min_data = new System.Windows.Forms.Label();
            this.EA_data = new System.Windows.Forms.Label();
            this.EB_data = new System.Windows.Forms.Label();
            this.EC_data = new System.Windows.Forms.Label();
            this.ET_data = new System.Windows.Forms.Label();
            this.PeakA_data = new System.Windows.Forms.Label();
            this.PeakB_data = new System.Windows.Forms.Label();
            this.PeakC_data = new System.Windows.Forms.Label();
            this.PeakT_data = new System.Windows.Forms.Label();
            this.Add_btn = new System.Windows.Forms.Button();
            this.Remove_btn = new System.Windows.Forms.Button();
            this.Device_lst = new System.Windows.Forms.ComboBox();
            this.Description_txt = new System.Windows.Forms.TextBox();
            this.splitContainer1 = new System.Windows.Forms.SplitContainer();
            this.Location_lbl = new System.Windows.Forms.Label();
            this.Device_lbl = new System.Windows.Forms.Label();
            this.Zone_lst = new System.Windows.Forms.ComboBox();
            this.List_lbl = new System.Windows.Forms.Label();
            this.Zone_lbl = new System.Windows.Forms.Label();
            this.Zone_txt = new System.Windows.Forms.TextBox();
            this.List_txt = new System.Windows.Forms.TextBox();
            this.Remove_zone_btn = new System.Windows.Forms.Button();
            this.Add_zone_btn = new System.Windows.Forms.Button();
            this.Zone_list_lbl = new System.Windows.Forms.Label();
            this.Current_node_lbl = new System.Windows.Forms.Label();
            this.zone_connect_btn = new System.Windows.Forms.Button();
            ((System.ComponentModel.ISupportInitialize)(this.splitContainer1)).BeginInit();
            this.splitContainer1.Panel1.SuspendLayout();
            this.splitContainer1.Panel2.SuspendLayout();
            this.splitContainer1.SuspendLayout();
            this.SuspendLayout();
            // 
            // checkBox1
            // 
            this.checkBox1.Appearance = System.Windows.Forms.Appearance.Button;
            this.checkBox1.AutoSize = true;
            this.checkBox1.Location = new System.Drawing.Point(9, 686);
            this.checkBox1.Name = "checkBox1";
            this.checkBox1.Size = new System.Drawing.Size(41, 23);
            this.checkBox1.TabIndex = 0;
            this.checkBox1.Text = " LED";
            this.checkBox1.UseVisualStyleBackColor = true;
            this.checkBox1.CheckedChanged += new System.EventHandler(this.checkBox1_CheckedChanged);
            // 
            // Payload_lbl
            // 
            this.Payload_lbl.AutoSize = true;
            this.Payload_lbl.Location = new System.Drawing.Point(76, 686);
            this.Payload_lbl.Name = "Payload_lbl";
            this.Payload_lbl.Size = new System.Drawing.Size(44, 13);
            this.Payload_lbl.TabIndex = 2;
            this.Payload_lbl.Text = "payload";
            this.Payload_lbl.Click += new System.EventHandler(this.label1_Click);
            // 
            // Topic_lbl
            // 
            this.Topic_lbl.AutoSize = true;
            this.Topic_lbl.Location = new System.Drawing.Point(199, 686);
            this.Topic_lbl.Name = "Topic_lbl";
            this.Topic_lbl.Size = new System.Drawing.Size(30, 13);
            this.Topic_lbl.TabIndex = 3;
            this.Topic_lbl.Text = "topic";
            this.Topic_lbl.Click += new System.EventHandler(this.label2_Click);
            // 
            // NID_lbl
            // 
            this.NID_lbl.AutoSize = true;
            this.NID_lbl.Location = new System.Drawing.Point(3, 14);
            this.NID_lbl.Name = "NID_lbl";
            this.NID_lbl.Size = new System.Drawing.Size(50, 13);
            this.NID_lbl.TabIndex = 5;
            this.NID_lbl.Text = "Node ID:";
            this.NID_lbl.Click += new System.EventHandler(this.label3_Click);
            // 
            // PA_lbl
            // 
            this.PA_lbl.AutoSize = true;
            this.PA_lbl.Location = new System.Drawing.Point(54, 74);
            this.PA_lbl.Name = "PA_lbl";
            this.PA_lbl.Size = new System.Drawing.Size(24, 13);
            this.PA_lbl.TabIndex = 6;
            this.PA_lbl.Text = "PA:";
            // 
            // PA_data
            // 
            this.PA_data.AutoSize = true;
            this.PA_data.Location = new System.Drawing.Point(151, 74);
            this.PA_data.Name = "PA_data";
            this.PA_data.Size = new System.Drawing.Size(30, 13);
            this.PA_data.TabIndex = 7;
            this.PA_data.Text = "NAN";
            this.PA_data.Click += new System.EventHandler(this.label3_Click_1);
            // 
            // NID_textbox
            // 
            this.NID_textbox.Location = new System.Drawing.Point(6, 30);
            this.NID_textbox.Name = "NID_textbox";
            this.NID_textbox.Size = new System.Drawing.Size(100, 20);
            this.NID_textbox.TabIndex = 0;
            this.NID_textbox.Text = "fff1";
            // 
            // Connect_btn
            // 
            this.Connect_btn.Location = new System.Drawing.Point(248, 103);
            this.Connect_btn.Name = "Connect_btn";
            this.Connect_btn.Size = new System.Drawing.Size(75, 23);
            this.Connect_btn.TabIndex = 3;
            this.Connect_btn.Text = "Connect";
            this.Connect_btn.UseVisualStyleBackColor = true;
            this.Connect_btn.Click += new System.EventHandler(this.Connect_btn_Click);
            // 
            // PB_lbl
            // 
            this.PB_lbl.AutoSize = true;
            this.PB_lbl.Location = new System.Drawing.Point(54, 101);
            this.PB_lbl.Name = "PB_lbl";
            this.PB_lbl.Size = new System.Drawing.Size(24, 13);
            this.PB_lbl.TabIndex = 10;
            this.PB_lbl.Text = "PB:";
            // 
            // PC_lbl
            // 
            this.PC_lbl.AutoSize = true;
            this.PC_lbl.Location = new System.Drawing.Point(54, 124);
            this.PC_lbl.Name = "PC_lbl";
            this.PC_lbl.Size = new System.Drawing.Size(24, 13);
            this.PC_lbl.TabIndex = 11;
            this.PC_lbl.Text = "PC:";
            // 
            // PB_data
            // 
            this.PB_data.AutoSize = true;
            this.PB_data.Location = new System.Drawing.Point(151, 101);
            this.PB_data.Name = "PB_data";
            this.PB_data.Size = new System.Drawing.Size(30, 13);
            this.PB_data.TabIndex = 12;
            this.PB_data.Text = "NAN";
            // 
            // PC_data
            // 
            this.PC_data.AutoSize = true;
            this.PC_data.Location = new System.Drawing.Point(151, 124);
            this.PC_data.Name = "PC_data";
            this.PC_data.Size = new System.Drawing.Size(30, 13);
            this.PC_data.TabIndex = 13;
            this.PC_data.Text = "NAN";
            // 
            // PT_lbl
            // 
            this.PT_lbl.AutoSize = true;
            this.PT_lbl.Location = new System.Drawing.Point(54, 147);
            this.PT_lbl.Name = "PT_lbl";
            this.PT_lbl.Size = new System.Drawing.Size(24, 13);
            this.PT_lbl.TabIndex = 14;
            this.PT_lbl.Text = "PT:";
            // 
            // PT_data
            // 
            this.PT_data.AutoSize = true;
            this.PT_data.Location = new System.Drawing.Point(151, 147);
            this.PT_data.Name = "PT_data";
            this.PT_data.Size = new System.Drawing.Size(30, 13);
            this.PT_data.TabIndex = 15;
            this.PT_data.Text = "NAN";
            // 
            // Pavg_lbl
            // 
            this.Pavg_lbl.AutoSize = true;
            this.Pavg_lbl.Location = new System.Drawing.Point(54, 170);
            this.Pavg_lbl.Name = "Pavg_lbl";
            this.Pavg_lbl.Size = new System.Drawing.Size(35, 13);
            this.Pavg_lbl.TabIndex = 16;
            this.Pavg_lbl.Text = "Pavg:";
            // 
            // Pavg_min_lbl
            // 
            this.Pavg_min_lbl.AutoSize = true;
            this.Pavg_min_lbl.Location = new System.Drawing.Point(54, 194);
            this.Pavg_min_lbl.Name = "Pavg_min_lbl";
            this.Pavg_min_lbl.Size = new System.Drawing.Size(56, 13);
            this.Pavg_min_lbl.TabIndex = 17;
            this.Pavg_min_lbl.Text = "Pavg/min:";
            // 
            // EA_lbl
            // 
            this.EA_lbl.AutoSize = true;
            this.EA_lbl.Location = new System.Drawing.Point(54, 216);
            this.EA_lbl.Name = "EA_lbl";
            this.EA_lbl.Size = new System.Drawing.Size(24, 13);
            this.EA_lbl.TabIndex = 18;
            this.EA_lbl.Text = "EA:";
            // 
            // EB_lbl
            // 
            this.EB_lbl.AutoSize = true;
            this.EB_lbl.Location = new System.Drawing.Point(54, 239);
            this.EB_lbl.Name = "EB_lbl";
            this.EB_lbl.Size = new System.Drawing.Size(24, 13);
            this.EB_lbl.TabIndex = 19;
            this.EB_lbl.Text = "EB:";
            // 
            // EC_lbl
            // 
            this.EC_lbl.AutoSize = true;
            this.EC_lbl.Location = new System.Drawing.Point(54, 261);
            this.EC_lbl.Name = "EC_lbl";
            this.EC_lbl.Size = new System.Drawing.Size(24, 13);
            this.EC_lbl.TabIndex = 20;
            this.EC_lbl.Text = "EC:";
            // 
            // ET_lbl
            // 
            this.ET_lbl.AutoSize = true;
            this.ET_lbl.Location = new System.Drawing.Point(54, 285);
            this.ET_lbl.Name = "ET_lbl";
            this.ET_lbl.Size = new System.Drawing.Size(24, 13);
            this.ET_lbl.TabIndex = 21;
            this.ET_lbl.Text = "ET:";
            // 
            // PeakA_lbl
            // 
            this.PeakA_lbl.AutoSize = true;
            this.PeakA_lbl.Location = new System.Drawing.Point(54, 309);
            this.PeakA_lbl.Name = "PeakA_lbl";
            this.PeakA_lbl.Size = new System.Drawing.Size(42, 13);
            this.PeakA_lbl.TabIndex = 22;
            this.PeakA_lbl.Text = "PeakA:";
            // 
            // PeakB_lbl
            // 
            this.PeakB_lbl.AutoSize = true;
            this.PeakB_lbl.Location = new System.Drawing.Point(54, 332);
            this.PeakB_lbl.Name = "PeakB_lbl";
            this.PeakB_lbl.Size = new System.Drawing.Size(42, 13);
            this.PeakB_lbl.TabIndex = 23;
            this.PeakB_lbl.Text = "PeakB:";
            // 
            // PeakC_lbl
            // 
            this.PeakC_lbl.AutoSize = true;
            this.PeakC_lbl.Location = new System.Drawing.Point(54, 354);
            this.PeakC_lbl.Name = "PeakC_lbl";
            this.PeakC_lbl.Size = new System.Drawing.Size(42, 13);
            this.PeakC_lbl.TabIndex = 24;
            this.PeakC_lbl.Text = "PeakC:";
            // 
            // PeakT_lbl
            // 
            this.PeakT_lbl.AutoSize = true;
            this.PeakT_lbl.Location = new System.Drawing.Point(54, 376);
            this.PeakT_lbl.Name = "PeakT_lbl";
            this.PeakT_lbl.Size = new System.Drawing.Size(42, 13);
            this.PeakT_lbl.TabIndex = 25;
            this.PeakT_lbl.Text = "PeakT:";
            // 
            // Pavg_data
            // 
            this.Pavg_data.AutoSize = true;
            this.Pavg_data.Location = new System.Drawing.Point(151, 170);
            this.Pavg_data.Name = "Pavg_data";
            this.Pavg_data.Size = new System.Drawing.Size(30, 13);
            this.Pavg_data.TabIndex = 26;
            this.Pavg_data.Text = "NAN";
            // 
            // Pavg_min_data
            // 
            this.Pavg_min_data.AutoSize = true;
            this.Pavg_min_data.Location = new System.Drawing.Point(151, 194);
            this.Pavg_min_data.Name = "Pavg_min_data";
            this.Pavg_min_data.Size = new System.Drawing.Size(30, 13);
            this.Pavg_min_data.TabIndex = 27;
            this.Pavg_min_data.Text = "NAN";
            // 
            // EA_data
            // 
            this.EA_data.AutoSize = true;
            this.EA_data.Location = new System.Drawing.Point(151, 216);
            this.EA_data.Name = "EA_data";
            this.EA_data.Size = new System.Drawing.Size(30, 13);
            this.EA_data.TabIndex = 28;
            this.EA_data.Text = "NAN";
            // 
            // EB_data
            // 
            this.EB_data.AutoSize = true;
            this.EB_data.Location = new System.Drawing.Point(151, 239);
            this.EB_data.Name = "EB_data";
            this.EB_data.Size = new System.Drawing.Size(30, 13);
            this.EB_data.TabIndex = 29;
            this.EB_data.Text = "NAN";
            // 
            // EC_data
            // 
            this.EC_data.AutoSize = true;
            this.EC_data.Location = new System.Drawing.Point(151, 261);
            this.EC_data.Name = "EC_data";
            this.EC_data.Size = new System.Drawing.Size(30, 13);
            this.EC_data.TabIndex = 30;
            this.EC_data.Text = "NAN";
            // 
            // ET_data
            // 
            this.ET_data.AutoSize = true;
            this.ET_data.Location = new System.Drawing.Point(151, 285);
            this.ET_data.Name = "ET_data";
            this.ET_data.Size = new System.Drawing.Size(30, 13);
            this.ET_data.TabIndex = 31;
            this.ET_data.Text = "NAN";
            // 
            // PeakA_data
            // 
            this.PeakA_data.AutoSize = true;
            this.PeakA_data.Location = new System.Drawing.Point(151, 309);
            this.PeakA_data.Name = "PeakA_data";
            this.PeakA_data.Size = new System.Drawing.Size(30, 13);
            this.PeakA_data.TabIndex = 32;
            this.PeakA_data.Text = "NAN";
            // 
            // PeakB_data
            // 
            this.PeakB_data.AutoSize = true;
            this.PeakB_data.Location = new System.Drawing.Point(151, 332);
            this.PeakB_data.Name = "PeakB_data";
            this.PeakB_data.Size = new System.Drawing.Size(30, 13);
            this.PeakB_data.TabIndex = 33;
            this.PeakB_data.Text = "NAN";
            // 
            // PeakC_data
            // 
            this.PeakC_data.AutoSize = true;
            this.PeakC_data.Location = new System.Drawing.Point(151, 354);
            this.PeakC_data.Name = "PeakC_data";
            this.PeakC_data.Size = new System.Drawing.Size(30, 13);
            this.PeakC_data.TabIndex = 34;
            this.PeakC_data.Text = "NAN";
            // 
            // PeakT_data
            // 
            this.PeakT_data.AutoSize = true;
            this.PeakT_data.Location = new System.Drawing.Point(151, 376);
            this.PeakT_data.Name = "PeakT_data";
            this.PeakT_data.Size = new System.Drawing.Size(30, 13);
            this.PeakT_data.TabIndex = 35;
            this.PeakT_data.Text = "NAN";
            // 
            // Add_btn
            // 
            this.Add_btn.Location = new System.Drawing.Point(145, 74);
            this.Add_btn.Name = "Add_btn";
            this.Add_btn.Size = new System.Drawing.Size(75, 23);
            this.Add_btn.TabIndex = 2;
            this.Add_btn.Text = "Add";
            this.Add_btn.UseVisualStyleBackColor = true;
            this.Add_btn.Click += new System.EventHandler(this.Add_btn_Click);
            // 
            // Remove_btn
            // 
            this.Remove_btn.Location = new System.Drawing.Point(248, 74);
            this.Remove_btn.Name = "Remove_btn";
            this.Remove_btn.Size = new System.Drawing.Size(75, 23);
            this.Remove_btn.TabIndex = 5;
            this.Remove_btn.Text = "Remove";
            this.Remove_btn.UseVisualStyleBackColor = true;
            this.Remove_btn.Click += new System.EventHandler(this.Remove_btn_Click);
            // 
            // Device_lst
            // 
            this.Device_lst.FormattingEnabled = true;
            this.Device_lst.Location = new System.Drawing.Point(145, 132);
            this.Device_lst.Name = "Device_lst";
            this.Device_lst.Size = new System.Drawing.Size(178, 21);
            this.Device_lst.TabIndex = 6;
            this.Device_lst.SelectedIndexChanged += new System.EventHandler(this.Device_lst_SelectedIndexChanged);
            // 
            // Description_txt
            // 
            this.Description_txt.Location = new System.Drawing.Point(145, 30);
            this.Description_txt.Name = "Description_txt";
            this.Description_txt.Size = new System.Drawing.Size(178, 20);
            this.Description_txt.TabIndex = 1;
            // 
            // splitContainer1
            // 
            this.splitContainer1.Location = new System.Drawing.Point(9, 31);
            this.splitContainer1.Name = "splitContainer1";
            // 
            // splitContainer1.Panel1
            // 
            this.splitContainer1.Panel1.BackColor = System.Drawing.SystemColors.ButtonFace;
            this.splitContainer1.Panel1.Controls.Add(this.zone_connect_btn);
            this.splitContainer1.Panel1.Controls.Add(this.Zone_list_lbl);
            this.splitContainer1.Panel1.Controls.Add(this.Remove_zone_btn);
            this.splitContainer1.Panel1.Controls.Add(this.Add_zone_btn);
            this.splitContainer1.Panel1.Controls.Add(this.List_lbl);
            this.splitContainer1.Panel1.Controls.Add(this.Zone_lbl);
            this.splitContainer1.Panel1.Controls.Add(this.Zone_txt);
            this.splitContainer1.Panel1.Controls.Add(this.List_txt);
            this.splitContainer1.Panel1.Controls.Add(this.Zone_lst);
            this.splitContainer1.Panel1.Controls.Add(this.Device_lbl);
            this.splitContainer1.Panel1.Controls.Add(this.Location_lbl);
            this.splitContainer1.Panel1.Controls.Add(this.NID_lbl);
            this.splitContainer1.Panel1.Controls.Add(this.Remove_btn);
            this.splitContainer1.Panel1.Controls.Add(this.Connect_btn);
            this.splitContainer1.Panel1.Controls.Add(this.Add_btn);
            this.splitContainer1.Panel1.Controls.Add(this.Device_lst);
            this.splitContainer1.Panel1.Controls.Add(this.NID_textbox);
            this.splitContainer1.Panel1.Controls.Add(this.Description_txt);
            this.splitContainer1.Panel1.Paint += new System.Windows.Forms.PaintEventHandler(this.splitContainer1_Panel1_Paint);
            // 
            // splitContainer1.Panel2
            // 
            this.splitContainer1.Panel2.BackColor = System.Drawing.SystemColors.Window;
            this.splitContainer1.Panel2.Controls.Add(this.Current_node_lbl);
            this.splitContainer1.Panel2.Controls.Add(this.PeakA_data);
            this.splitContainer1.Panel2.Controls.Add(this.PeakB_data);
            this.splitContainer1.Panel2.Controls.Add(this.PA_lbl);
            this.splitContainer1.Panel2.Controls.Add(this.PeakC_lbl);
            this.splitContainer1.Panel2.Controls.Add(this.PA_data);
            this.splitContainer1.Panel2.Controls.Add(this.PeakB_lbl);
            this.splitContainer1.Panel2.Controls.Add(this.PB_lbl);
            this.splitContainer1.Panel2.Controls.Add(this.PeakT_lbl);
            this.splitContainer1.Panel2.Controls.Add(this.PC_lbl);
            this.splitContainer1.Panel2.Controls.Add(this.PeakA_lbl);
            this.splitContainer1.Panel2.Controls.Add(this.PB_data);
            this.splitContainer1.Panel2.Controls.Add(this.Pavg_data);
            this.splitContainer1.Panel2.Controls.Add(this.PeakT_data);
            this.splitContainer1.Panel2.Controls.Add(this.ET_lbl);
            this.splitContainer1.Panel2.Controls.Add(this.PC_data);
            this.splitContainer1.Panel2.Controls.Add(this.Pavg_min_data);
            this.splitContainer1.Panel2.Controls.Add(this.PeakC_data);
            this.splitContainer1.Panel2.Controls.Add(this.EC_lbl);
            this.splitContainer1.Panel2.Controls.Add(this.PT_lbl);
            this.splitContainer1.Panel2.Controls.Add(this.EA_data);
            this.splitContainer1.Panel2.Controls.Add(this.EB_lbl);
            this.splitContainer1.Panel2.Controls.Add(this.PT_data);
            this.splitContainer1.Panel2.Controls.Add(this.EB_data);
            this.splitContainer1.Panel2.Controls.Add(this.Pavg_lbl);
            this.splitContainer1.Panel2.Controls.Add(this.EA_lbl);
            this.splitContainer1.Panel2.Controls.Add(this.ET_data);
            this.splitContainer1.Panel2.Controls.Add(this.EC_data);
            this.splitContainer1.Panel2.Controls.Add(this.Pavg_min_lbl);
            this.splitContainer1.Size = new System.Drawing.Size(635, 622);
            this.splitContainer1.SplitterDistance = 342;
            this.splitContainer1.TabIndex = 36;
            // 
            // Location_lbl
            // 
            this.Location_lbl.AutoSize = true;
            this.Location_lbl.Location = new System.Drawing.Point(142, 14);
            this.Location_lbl.Name = "Location_lbl";
            this.Location_lbl.Size = new System.Drawing.Size(51, 13);
            this.Location_lbl.TabIndex = 7;
            this.Location_lbl.Text = "Location:";
            // 
            // Device_lbl
            // 
            this.Device_lbl.AutoSize = true;
            this.Device_lbl.Location = new System.Drawing.Point(142, 113);
            this.Device_lbl.Name = "Device_lbl";
            this.Device_lbl.Size = new System.Drawing.Size(44, 13);
            this.Device_lbl.TabIndex = 8;
            this.Device_lbl.Text = "Device:";
            // 
            // Zone_lst
            // 
            this.Zone_lst.FormattingEnabled = true;
            this.Zone_lst.Location = new System.Drawing.Point(145, 232);
            this.Zone_lst.Name = "Zone_lst";
            this.Zone_lst.Size = new System.Drawing.Size(178, 21);
            this.Zone_lst.TabIndex = 9;
            // 
            // List_lbl
            // 
            this.List_lbl.AutoSize = true;
            this.List_lbl.Location = new System.Drawing.Point(142, 170);
            this.List_lbl.Name = "List_lbl";
            this.List_lbl.Size = new System.Drawing.Size(23, 13);
            this.List_lbl.TabIndex = 13;
            this.List_lbl.Text = "List";
            // 
            // Zone_lbl
            // 
            this.Zone_lbl.AutoSize = true;
            this.Zone_lbl.Location = new System.Drawing.Point(4, 171);
            this.Zone_lbl.Name = "Zone_lbl";
            this.Zone_lbl.Size = new System.Drawing.Size(49, 13);
            this.Zone_lbl.TabIndex = 12;
            this.Zone_lbl.Text = "Zone ID:";
            this.Zone_lbl.Click += new System.EventHandler(this.Zone_lbl_Click);
            // 
            // Zone_txt
            // 
            this.Zone_txt.Location = new System.Drawing.Point(6, 187);
            this.Zone_txt.Name = "Zone_txt";
            this.Zone_txt.Size = new System.Drawing.Size(100, 20);
            this.Zone_txt.TabIndex = 10;
            this.Zone_txt.Text = "A";
            // 
            // List_txt
            // 
            this.List_txt.Location = new System.Drawing.Point(145, 187);
            this.List_txt.Name = "List_txt";
            this.List_txt.Size = new System.Drawing.Size(178, 20);
            this.List_txt.TabIndex = 11;
            // 
            // Remove_zone_btn
            // 
            this.Remove_zone_btn.Location = new System.Drawing.Point(248, 277);
            this.Remove_zone_btn.Name = "Remove_zone_btn";
            this.Remove_zone_btn.Size = new System.Drawing.Size(75, 23);
            this.Remove_zone_btn.TabIndex = 15;
            this.Remove_zone_btn.Text = "Remove";
            this.Remove_zone_btn.UseVisualStyleBackColor = true;
            this.Remove_zone_btn.Click += new System.EventHandler(this.Remove_zone_btn_Click);
            // 
            // Add_zone_btn
            // 
            this.Add_zone_btn.Location = new System.Drawing.Point(145, 277);
            this.Add_zone_btn.Name = "Add_zone_btn";
            this.Add_zone_btn.Size = new System.Drawing.Size(75, 23);
            this.Add_zone_btn.TabIndex = 14;
            this.Add_zone_btn.Text = "Add";
            this.Add_zone_btn.UseVisualStyleBackColor = true;
            this.Add_zone_btn.Click += new System.EventHandler(this.Add_zone_btn_Click);
            // 
            // Zone_list_lbl
            // 
            this.Zone_list_lbl.AutoSize = true;
            this.Zone_list_lbl.Location = new System.Drawing.Point(142, 216);
            this.Zone_list_lbl.Name = "Zone_list_lbl";
            this.Zone_list_lbl.Size = new System.Drawing.Size(35, 13);
            this.Zone_list_lbl.TabIndex = 16;
            this.Zone_list_lbl.Text = "Zone:";
            // 
            // Current_node_lbl
            // 
            this.Current_node_lbl.AutoSize = true;
            this.Current_node_lbl.Location = new System.Drawing.Point(3, 14);
            this.Current_node_lbl.Name = "Current_node_lbl";
            this.Current_node_lbl.Size = new System.Drawing.Size(36, 13);
            this.Current_node_lbl.TabIndex = 36;
            this.Current_node_lbl.Text = "Node:";
            // 
            // zone_connect_btn
            // 
            this.zone_connect_btn.Location = new System.Drawing.Point(248, 309);
            this.zone_connect_btn.Name = "zone_connect_btn";
            this.zone_connect_btn.Size = new System.Drawing.Size(75, 23);
            this.zone_connect_btn.TabIndex = 17;
            this.zone_connect_btn.Text = "Connect";
            this.zone_connect_btn.UseVisualStyleBackColor = true;
            this.zone_connect_btn.Click += new System.EventHandler(this.zone_connect_btn_Click);
            // 
            // Form1
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.BackColor = System.Drawing.SystemColors.ControlLightLight;
            this.ClientSize = new System.Drawing.Size(1344, 721);
            this.Controls.Add(this.splitContainer1);
            this.Controls.Add(this.Topic_lbl);
            this.Controls.Add(this.Payload_lbl);
            this.Controls.Add(this.checkBox1);
            this.Name = "Form1";
            this.Text = "WPM2018a";
            this.Load += new System.EventHandler(this.Form1_Load);
            this.splitContainer1.Panel1.ResumeLayout(false);
            this.splitContainer1.Panel1.PerformLayout();
            this.splitContainer1.Panel2.ResumeLayout(false);
            this.splitContainer1.Panel2.PerformLayout();
            ((System.ComponentModel.ISupportInitialize)(this.splitContainer1)).EndInit();
            this.splitContainer1.ResumeLayout(false);
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.CheckBox checkBox1;
        private System.Windows.Forms.Label Payload_lbl;
        private System.Windows.Forms.Label Topic_lbl;
        private System.Windows.Forms.Label NID_lbl;
        private System.Windows.Forms.Label PA_lbl;
        private System.Windows.Forms.Label PA_data;
        private System.Windows.Forms.TextBox NID_textbox;
        private System.Windows.Forms.Button Connect_btn;
        private System.Windows.Forms.Label PB_lbl;
        private System.Windows.Forms.Label PC_lbl;
        private System.Windows.Forms.Label PB_data;
        private System.Windows.Forms.Label PC_data;
        private System.Windows.Forms.Label PT_lbl;
        private System.Windows.Forms.Label PT_data;
        private System.Windows.Forms.Label Pavg_lbl;
        private System.Windows.Forms.Label Pavg_min_lbl;
        private System.Windows.Forms.Label EA_lbl;
        private System.Windows.Forms.Label EB_lbl;
        private System.Windows.Forms.Label EC_lbl;
        private System.Windows.Forms.Label ET_lbl;
        private System.Windows.Forms.Label PeakA_lbl;
        private System.Windows.Forms.Label PeakB_lbl;
        private System.Windows.Forms.Label PeakC_lbl;
        private System.Windows.Forms.Label PeakT_lbl;
        private System.Windows.Forms.Label Pavg_data;
        private System.Windows.Forms.Label Pavg_min_data;
        private System.Windows.Forms.Label EA_data;
        private System.Windows.Forms.Label EB_data;
        private System.Windows.Forms.Label EC_data;
        private System.Windows.Forms.Label ET_data;
        private System.Windows.Forms.Label PeakA_data;
        private System.Windows.Forms.Label PeakB_data;
        private System.Windows.Forms.Label PeakC_data;
        private System.Windows.Forms.Label PeakT_data;
        private System.Windows.Forms.Button Add_btn;
        private System.Windows.Forms.Button Remove_btn;
        private System.Windows.Forms.ComboBox Device_lst;
        private System.Windows.Forms.TextBox Description_txt;
        private System.Windows.Forms.SplitContainer splitContainer1;
        private System.Windows.Forms.Button Remove_zone_btn;
        private System.Windows.Forms.Button Add_zone_btn;
        private System.Windows.Forms.Label List_lbl;
        private System.Windows.Forms.Label Zone_lbl;
        private System.Windows.Forms.TextBox Zone_txt;
        private System.Windows.Forms.TextBox List_txt;
        private System.Windows.Forms.ComboBox Zone_lst;
        private System.Windows.Forms.Label Device_lbl;
        private System.Windows.Forms.Label Location_lbl;
        private System.Windows.Forms.Label Zone_list_lbl;
        private System.Windows.Forms.Label Current_node_lbl;
        private System.Windows.Forms.Button zone_connect_btn;
    }
}

